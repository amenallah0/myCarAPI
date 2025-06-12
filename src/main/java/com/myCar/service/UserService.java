package com.myCar.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.transaction.annotation.Transactional;

import com.myCar.domain.User;
import com.myCar.repository.UserRepository;
import com.myCar.usecase.UserUseCase;
import com.myCar.domain.Role;
import com.myCar.security.AuthResponse;
import com.myCar.security.JwtTokenProvider;
import com.myCar.security.TokenResponse;
import com.myCar.repository.ExpertiseRequestRepository;
import com.myCar.domain.ExpertiseRequest;
import io.jsonwebtoken.Claims;
import com.myCar.repository.ExpertReportRepository;
import com.myCar.service.ExpertRequestService;

@Service
@Transactional
public class UserService implements UserUseCase {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Inject PasswordEncoder bean here
    private final JwtTokenProvider jwtTokenProvider;
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private final Map<String, Integer> loginAttempts = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> lockoutTimes = new ConcurrentHashMap<>();

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ExpertiseRequestRepository expertiseRequestRepository;

    @Autowired
    private ExpertRequestService expertRequestService;

    @Autowired
    private ExpertReportRepository expertReportRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder; // Assign the injected PasswordEncoder to the local variable
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long userId) {
        // 1. Supprimer les rapports d'expertise
        expertReportRepository.deleteByUserId(userId);
        
        // 2. Supprimer les demandes d'expertise
        expertRequestService.deleteRequestsByUserId(userId);
        
        // 3. Supprimer l'utilisateur
        userRepository.deleteById(userId);
    }
    
    public List<User> findAllByRole(String roleStr) {
        try {
            logger.info("Finding users with role: {}", roleStr);
            // Ajouter le préfixe ROLE_ si nécessaire
            if (!roleStr.startsWith("ROLE_")) {
                roleStr = "ROLE_" + roleStr;
            }
            Role role = Role.valueOf(roleStr);
            List<User> users = userRepository.findByRole(role);
            logger.info("Found {} users with role {}", users.size(), role);
            return users;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid role: {}", roleStr, e);
            throw new IllegalArgumentException("Invalid role: " + roleStr);
        } catch (Exception e) {
            logger.error("Error finding users by role: {}", roleStr, e);
            throw new RuntimeException("Error finding users by role: " + e.getMessage());
        }
    }

    public User getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new RuntimeException("Non autorisé");
            }

            // Extract JWT from the SecurityContext (from the Bearer token in the header)
            String jwt = authentication.getCredentials().toString();
            Long userId = Long.parseLong(jwtTokenProvider.getUserIdFromJWT(jwt));
            User user = getUserById(userId);

            if (user == null) {
                throw new RuntimeException("Utilisateur non trouvé");
            }

            return user;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération du profil: " + e.getMessage());
        }
    }
    
    @Override
    public AuthResponse authenticateUser(String email, String password) {
        try {
            System.out.println("Tentative de connexion pour : " + email + " avec le mot de passe : " + password);
            User user = getUserByEmail(email);
            if (user != null) {
                System.out.println("Mot de passe hashé en base : " + user.getPassword());
            } else {
                System.out.println("Aucun utilisateur trouvé avec cet email.");
            }

            // Authentifier l'utilisateur
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );
            
            // Récupérer l'utilisateur
            user = getUserByEmail(email);
            if (user == null) {
                throw new RuntimeException("Utilisateur non trouvé");
            }
            
            // Générer les tokens
            String accessToken = jwtTokenProvider.generateToken(user);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user);
            
            // Créer TokenResponse avec une durée d'expiration fixe (par exemple 3600 secondes = 1 heure)
            TokenResponse tokens = new TokenResponse(
                accessToken,
                refreshToken,
                3600L  // durée d'expiration en secondes
            );
            
            // Retourner AuthResponse avec TokenResponse et User
            return new AuthResponse(tokens, user);
            
        } catch (Exception e) {
            System.out.println("Erreur d'authentification : " + e.getMessage());
            throw new RuntimeException("Erreur d'authentification: " + e.getMessage());
        }
    }

    private void checkLoginAttempts(String email) {
        LocalDateTime lockoutTime = lockoutTimes.get(email);
        if (lockoutTime != null) {
            if (LocalDateTime.now().isBefore(lockoutTime)) {
                throw new BadCredentialsException("Account is temporarily locked");
            } else {
                lockoutTimes.remove(email);
                loginAttempts.remove(email);
            }
        }
    }

    private void incrementLoginAttempts(String email) {
        int attempts = loginAttempts.getOrDefault(email, 0) + 1;
        loginAttempts.put(email, attempts);

        if (attempts >= MAX_LOGIN_ATTEMPTS) {
            lockoutTimes.put(email, LocalDateTime.now().plusMinutes(15));
            throw new BadCredentialsException("Too many failed attempts. Account locked for 15 minutes");
        }
    }
    @GetMapping("/profile")
    public ResponseEntity<User> getCurrentUserProfile(@RequestHeader("Authorization") String token) {
        try {
            User currentUser = getCurrentUser();
            if (currentUser != null) {
                return ResponseEntity.ok(currentUser);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private void resetLoginAttempts(String email) {
        loginAttempts.remove(email);
        lockoutTimes.remove(email);
    }

    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id).map(user -> {
            if (updatedUser.getUsername() != null) {
                user.setUsername(updatedUser.getUsername());
            }
            if (updatedUser.getFirstName() != null) {
                user.setFirstName(updatedUser.getFirstName());
            }
            if (updatedUser.getLastName() != null) {
                user.setLastName(updatedUser.getLastName());
            }
            if (updatedUser.getPhone() != null) {
                user.setPhone(updatedUser.getPhone());
            }
            if (updatedUser.getAddress() != null) {
                user.setAddress(updatedUser.getAddress());
            }
            return userRepository.save(user);
        }).orElse(null);
    }
    
    public Map<String, Long> getExpertiseCount(Long userId) {
        Map<String, Long> counts = new HashMap<>();
        try {
            logger.info("Starting getExpertiseCount for userId: {}", userId);

            // Log all requests for this user first
            List<ExpertiseRequest> allRequests = expertiseRequestRepository.findByUserId(userId);
            logger.info("Total requests found for user {}: {}", userId, allRequests.size());
            
            // Log details of each request
            allRequests.forEach(request -> {
                logger.info("Request ID: {}, Status: {}, Expert: {}, User: {}, Car: {}, Report: {}",
                    request.getId(),
                    request.getStatus(),
                    request.getExpert() != null ? request.getExpert().getId() : "null",
                    request.getUser() != null ? request.getUser().getId() : "null",
                    request.getCar() != null ? request.getCar().getId() : "null",
                    request.getReport() != null ? "present" : "null"
                );
            });

            // Get counts with detailed logging
            Long totalCount = expertiseRequestRepository.countByUserId(userId);
            logger.info("Total count for user {}: {}", userId, totalCount);

            Long completedCount = expertiseRequestRepository.countByUserIdAndStatus(userId, ExpertiseRequest.RequestStatus.COMPLETED);
            logger.info("Completed count for user {}: {}", userId, completedCount);

            Long pendingCount = expertiseRequestRepository.countByUserIdAndStatus(userId, ExpertiseRequest.RequestStatus.PENDING);
            logger.info("Pending count for user {}: {}", userId, pendingCount);

            // Get status breakdown
            List<Object[]> statusCounts = expertiseRequestRepository.getStatusCountsByUserId(userId);
            logger.info("Status breakdown for user {}:", userId);
            statusCounts.forEach(count -> 
                logger.info("Status: {}, Count: {}", count[0], count[1])
            );

            counts.put("total", totalCount != null ? totalCount : 0L);
            counts.put("completed", completedCount != null ? completedCount : 0L);
            counts.put("pending", pendingCount != null ? pendingCount : 0L);

            logger.info("Final counts map for user {}: {}", userId, counts);
            return counts;
            
        } catch (Exception e) {
            logger.error("Error in getExpertiseCount for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error counting expertise requests: " + e.getMessage());
        }
    }

    private void debugExpertiseRequests(Long userId) {
        try {
            List<ExpertiseRequest> requests = expertiseRequestRepository.findAllRequestsForUserDebug(userId);
            logger.info("Raw database records for user {}:", userId);
            requests.forEach(request -> {
                logger.info("ID: {}, Status: {}, Created: {}, User ID: {}", 
                    request.getId(), 
                    request.getStatus(),
                    request.getRequestDate(),
                    request.getUser() != null ? request.getUser().getId() : null
                );
            });
        } catch (Exception e) {
            logger.error("Error in debug query", e);
        }
    }

    public Long getExpertRequestCount(Long expertId) {
        return expertiseRequestRepository.countByExpertId(expertId);
    }

    public AuthResponse refreshToken(String refreshToken) {
        try {
            // Validate the refresh token
            Claims claims = jwtTokenProvider.validateAndGetClaims(refreshToken);
            
            // Check if it's a refresh token
            if (!"refresh".equals(claims.get("type"))) {
                throw new RuntimeException("Invalid token type");
            }
            
            // Get user from the token
            Long userId = Long.parseLong(claims.getSubject());
            User user = getUserById(userId);
            
            if (user == null) {
                throw new RuntimeException("User not found");
            }
            
            // Generate new token pair
            String newAccessToken = jwtTokenProvider.generateToken(user);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);
            
            // Create TokenResponse with expiration
            TokenResponse tokens = new TokenResponse(
                newAccessToken,
                newRefreshToken,
                3600L  // 1 hour expiration
            );
            
            // Return new AuthResponse
            return new AuthResponse(tokens, user);
            
        } catch (Exception e) {
            throw new RuntimeException("Token refresh failed: " + e.getMessage());
        }
    }
}



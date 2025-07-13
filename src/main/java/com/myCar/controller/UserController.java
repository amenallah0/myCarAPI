package com.myCar.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.myCar.controller.ExpertiseRequestController.ErrorResponse;
import com.myCar.domain.Role;
import com.myCar.domain.User;
import com.myCar.service.UserService;
import com.myCar.security.AuthResponse;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User user = userService.getUserByEmail(email);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser, Principal principal) {
        System.out.println("[BACKEND] PUT /api/users/" + id + " called.");
        System.out.println("[BACKEND] Authenticated user: " + (principal != null ? principal.getName() : "N/A"));
        User user = userService.updateUser(id, updatedUser);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/signin")
    public ResponseEntity<?> signInUser(@RequestBody SignInRequest signInRequest) {
        try {
            AuthResponse authResponse = userService.authenticateUser(signInRequest.getEmail(), signInRequest.getPassword());
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Authentication failed", e.getMessage()));
        }
    }

    @PostMapping("/expert/signup")
    public ResponseEntity<User> createExpertUser(@RequestBody User user) {
        user.setRole(Role.ROLE_EXPERT);
        User savedUser = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @PostMapping("/admin/signup")
    public ResponseEntity<User> createAdminUser(@RequestBody User user) {
        user.setRole(Role.ROLE_ADMIN);
        User savedUser = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/experts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllExperts() {
        try {
            logger.info("Fetching all experts");
            List<User> experts = userService.findAllByRole("EXPERT");
            logger.info("Found {} experts", experts.size());
            return ResponseEntity.ok(experts);
        } catch (Exception e) {
            logger.error("Error fetching experts: ", e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching experts: " + e.getMessage());
        }
    }
    @GetMapping("/experts/{expertId}/expertise-count")
    public ResponseEntity<Long> getExpertiseCountForExpert(@PathVariable Long expertId) {
        Long count = userService.getExpertRequestCount(expertId);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/profile")
    public ResponseEntity<User> getCurrentUserProfile() {
        System.out.println("[BACKEND] GET /api/users/profile called.");
        try {
            // Add a small delay to see if it's a timing issue
            try {
                Thread.sleep(1000); // 100 milliseconds delay
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }

            User currentUser = userService.getCurrentUser();
            System.out.println("[BACKEND] Successfully fetched current user profile.");
            return ResponseEntity.ok(currentUser);
        } catch (RuntimeException e) {
            System.err.println("[BACKEND] RuntimeException in /api/users/profile: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            System.err.println("[BACKEND] Exception in /api/users/profile: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/expertise-count")
    public ResponseEntity<Map<String, Long>> getExpertiseCount(@RequestParam(required = false) Long userId) {
        try {
            Map<String, Long> counts = userService.getExpertiseCount(userId);
            return ResponseEntity.ok(counts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody User user) {
        try {
            user.setAccountNonExpired(true);
            user.setAccountNonLocked(true);
            user.setCredentialsNonExpired(true);
            user.setEnabled(true);
            User savedUser = userService.saveUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            AuthResponse authResponse = userService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Token refresh failed", e.getMessage()));
        }
    }

    public static class SignInRequest {
        private String email;
        private String password;

        // getters and setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RefreshTokenRequest {
        private String refreshToken;

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }
}




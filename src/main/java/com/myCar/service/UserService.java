package com.myCar.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.myCar.domain.User;
import com.myCar.repository.UserRepository;
import com.myCar.usecase.UserUseCase;
import com.myCar.domain.Role;

@Service
public class UserService implements UserUseCase {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Inject PasswordEncoder bean here

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder; // Assign the injected PasswordEncoder to the local variable
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
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    @Override
    public User authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            // Authentification réussie, retourner l'utilisateur avec son rôle
            return user;
        }
        // Authentification échouée
        return null;
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
    
    public List<User> findAllByRole(String roleStr) {
        try {
            logger.info("Finding users with role: {}", roleStr);
            Role role = Role.valueOf(roleStr.toUpperCase());
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
}

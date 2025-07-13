package com.myCar.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.myCar.domain.Role;
import com.myCar.domain.User;
import com.myCar.service.UserService;

/**
 * Classe pour initialiser automatiquement le compte admin au démarrage de l'application
 */
@Component
public class AdminInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);
    
    private static final String ADMIN_EMAIL = "admin@mycar.com";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_FIRSTNAME = "Administrator";
    private static final String ADMIN_LASTNAME = "System";

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        logger.info("🚀 AdminInitializer starting...");
        createAdminUser();
    }

    private void createAdminUser() {
        try {
            // Vérifier si l'admin existe déjà par email
            User existingAdmin = userService.getUserByEmail(ADMIN_EMAIL);
            if (existingAdmin != null) {
                logger.info("✅ Admin user already exists: {}", ADMIN_EMAIL);
                return;
            }

            // Vérifier aussi par nom d'utilisateur
            User existingUser = userService.getUserByUsername(ADMIN_USERNAME);
            if (existingUser != null) {
                logger.info("✅ Admin user already exists with username: {}", ADMIN_USERNAME);
                return;
            }

            // Créer le compte admin
            User admin = new User(ADMIN_USERNAME, ADMIN_EMAIL, passwordEncoder.encode(ADMIN_PASSWORD), 
                                  Role.ROLE_ADMIN, ADMIN_FIRSTNAME, ADMIN_LASTNAME, null, null);
            admin.setEnabled(true);
            admin.setAccountNonExpired(true);
            admin.setAccountNonLocked(true);
            admin.setCredentialsNonExpired(true);

            userService.saveUser(admin);
            
            logger.info("🎉 Admin user created successfully!");
            logger.info("📧 Email: {}", ADMIN_EMAIL);
            logger.info("👤 Username: {}", ADMIN_USERNAME);
            logger.info("🔒 Password: {}", ADMIN_PASSWORD);
            logger.info("⚠️  IMPORTANT: Change the default password after first login!");
            
        } catch (Exception e) {
            logger.error("❌ Error creating admin user: {}", e.getMessage(), e);
        }
    }
}

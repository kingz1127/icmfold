package com.example.InnerCityBackend.config.setup;

import com.example.InnerCityBackend.model.entity.User;
import com.example.InnerCityBackend.model.enums.UserRole;
import com.example.InnerCityBackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

//        createSuperAdminIfNotExists("adebolaadebisi93@gmail.com", "Super", "Admin");
//        createSuperAdminIfNotExists("secondsuperadmin@gmail.com", "Second", "Super");
//        createSuperAdminIfNotExists("thirdsuperadmin@gmail.com", "Third", "Super");
        String adminEmail = "adebolaadebisi93@gmail.com"; // Set the email here once
        // 1. Check if the SPECIFIC Super Admin exists
        if (userRepository.findByEmail(adminEmail).isEmpty()) {

            User superAdmin = User.builder()
                    .firstName("Super")
                    .lastName("Admin")
                    .email(adminEmail)
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .role(UserRole.SUPER_ADMIN)
                    .emailVerified(true)
                    // REMOVED .enabled(true) because the field doesn't exist in your User entity
                    .build();

            userRepository.save(superAdmin);
            System.out.println("SUPER ADMIN ACCOUNT CREATED: " + adminEmail);
        }
    }

//    private void createSuperAdminIfNotExists(String email, String firstName, String lastName) {
//        if (userRepository.findByEmail(email).isEmpty()) {
//            User superAdmin = User.builder()
//                    .firstName(firstName)
//                    .lastName(lastName)
//                    .email(email)
//                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
//                    .role(UserRole.SUPER_ADMIN)
//                    .emailVerified(true)
//                    .build();
//            userRepository.save(superAdmin);
//            System.out.println("SUPER ADMIN ACCOUNT CREATED: " + email);
//        }
//    }
}
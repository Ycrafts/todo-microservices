// package com.authService.configs;

// import java.util.List;

// import java.util.Optional;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.crypto.password.PasswordEncoder;

// import com.authService.models.UserCredential;
// import com.authService.repositories.UserCredentialRepository;

// import lombok.RequiredArgsConstructor;

// @Configuration
// @RequiredArgsConstructor
// public class DataInitializer implements CommandLineRunner {


//     private final UserCredentialRepository userRepository;
//     private final PasswordEncoder passwordEncoder; 


//     @Override
//     public void run(String... args) throws Exception {
//         //create admin 
//         String adminUsername = "yonatan"; 
//             String adminEmail = "yonatanassefa60@gmail.com";
//             String adminPassword = "PassPass@123"; 

//             Optional<UserCredential> adminUserOptional = userRepository.findByUsername(adminUsername);

//             if (adminUserOptional.isEmpty()) {
//                 UserCredential adminUser = UserCredential.builder()
//                         .username(adminUsername)
//                         .email(adminEmail)
//                         .password(passwordEncoder.encode(adminPassword))
//                         .roles(List.of("ADMIN"))
//                         .build();
//                 userRepository.save(adminUser);
//                 System.out.println("Admin user created successfully.");
//             } else {
//                 System.out.println("Admin user already exists.");
//             }


//         UserCredential testUser = new UserCredential();
//         testUser.setUsername("testUser");
//         testUser.setEmail("testuser@example.com");
//         testUser.setPassword(passwordEncoder.encode("password123")); 
//         UserCredential savedTestUser = userRepository.save(testUser);
//     }
// }
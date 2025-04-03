package com.authService.services;

import java.time.Instant;
import java.util.List;
import java.util.UUID; 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService; 
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.authService.dtos.RegisterRequest;
import com.authService.dtos.RegisterResponse;
import com.authService.events.UserRegisteredEvent;
import com.authService.models.UserCredential;
import com.authService.repositories.UserCredentialRepository; 

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserCredentialService implements UserDetailsService{

    // private static final Logger logger = LoggerFactory.getLogger(UserCredentialService.class);

    private final UserCredentialRepository userCredentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final RabbitTemplate rabbitTemplate; 

    @Transactional
    public ResponseEntity<RegisterResponse> registerUser(RegisterRequest request) {
        // logger.info("Attempting to register user with username: {}", request.getUsername()); 
        if (userCredentialRepository.findByUsername(request.getUsername()).isPresent()) {
            // logger.warn("Username already exists: {}", request.getUsername());
            return new ResponseEntity<>(new RegisterResponse("Username already exists"), HttpStatus.BAD_REQUEST);
        }
        if (userCredentialRepository.findByEmail(request.getEmail()).isPresent()) {
            // logger.warn("Email already exists: {}", request.getEmail());
            return new ResponseEntity<>(new RegisterResponse("Email already exists"), HttpStatus.BAD_REQUEST);
        }

    
        String userId = UUID.randomUUID().toString();

        // System.out.println("the uuid before saving to db"+userId);

        UserCredential userCredential = UserCredential.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(List.of("USER"))
                .build();

        UserCredential savedUser = userCredentialRepository.save(userCredential);
        // logger.info("User saved successfully with ID: {}", savedUser.getId()); 

        userId = savedUser.getId().toString(); 

        
        UserRegisteredEvent userRegisteredEvent = new UserRegisteredEvent(
                userId,
                savedUser.getActualUsername(),
                savedUser.getEmail(),
                Instant.now() 
        );
        // logger.info("Created UserRegisteredEvent: {}", userRegisteredEvent); 
        
        rabbitTemplate.convertAndSend(
                "user.registered.exchange",
                "user.registered",
                userRegisteredEvent
        );
        // logger.info("Published UserRegisteredEvent to exchange: {}, routing key: {}", "user.registered.exchange", "user.registered"); // log  event publication

        return new ResponseEntity<>(new RegisterResponse("User registered successfully"), HttpStatus.CREATED);
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        return userCredentialRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail));
    }
}
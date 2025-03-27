package com.authService.services; // Adjust package name

import java.util.List;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService; // Import UUID for generating userId
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.authService.dtos.RegisterRequest;
import com.authService.dtos.RegisterResponse;
import com.authService.models.UserCredential;
import com.authService.repositories.UserCredentialRepository;

import lombok.RequiredArgsConstructor; // Import Transactional

import com.sharedEvents.events.UserRegisteredEvent;

@Service
@RequiredArgsConstructor
public class UserCredentialService implements UserDetailsService{

    private final UserCredentialRepository userCredentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final RabbitTemplate rabbitTemplate; // Inject RabbitTemplate

    @Transactional // Add this annotation
    public ResponseEntity<RegisterResponse> registerUser(RegisterRequest request) {
        if (userCredentialRepository.findByUsername(request.getUsername()).isPresent()) {
            return new ResponseEntity<>(new RegisterResponse("Username already exists"), HttpStatus.BAD_REQUEST);
        }
        if (userCredentialRepository.findByEmail(request.getEmail()).isPresent()) {
            return new ResponseEntity<>(new RegisterResponse("Email already exists"), HttpStatus.BAD_REQUEST);
        }

        // Generate a unique userId (as String for the event)
        String userId = UUID.randomUUID().toString();

        UserCredential userCredential = UserCredential.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(List.of("USER"))
                .build();

        userCredentialRepository.save(userCredential);

        rabbitTemplate.convertAndSend(
                "user.registered.exchange", // Use the exchange name defined in ProfileService
                "user.registered",         // Use the routing key defined in ProfileService
                new UserRegisteredEvent(userId, userCredential.getUsername(), userCredential.getEmail())
        );

        return new ResponseEntity<>(new RegisterResponse("User registered successfully"), HttpStatus.CREATED);
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        return userCredentialRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail));
    }

}
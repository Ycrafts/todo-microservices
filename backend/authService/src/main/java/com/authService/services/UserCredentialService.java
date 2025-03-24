package com.authService.services; // Adjust package name

import com.authService.models.UserCredential;
import com.authService.repositories.UserCredentialRepository;
import com.authService.dtos.RegisterRequest;
import com.authService.dtos.RegisterResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCredentialService {

    private final UserCredentialRepository userCredentialRepository;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<RegisterResponse> registerUser(RegisterRequest request) {
        if (userCredentialRepository.findByUsername(request.getUsername()).isPresent()) {
            return new ResponseEntity<>(new RegisterResponse("Username already exists"), HttpStatus.BAD_REQUEST);
        }
        if (userCredentialRepository.findByEmail(request.getEmail()).isPresent()) {
            return new ResponseEntity<>(new RegisterResponse("Email already exists"), HttpStatus.BAD_REQUEST);
        }

        UserCredential usercCredential = UserCredential.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(List.of("USER"))
                .build();

        userCredentialRepository.save(usercCredential);
        return new ResponseEntity<>(new RegisterResponse("User registered successfully"), HttpStatus.CREATED);
    }

    

}
package com.authService.services; // Adjust package name

import com.authService.models.UserCredential;
import com.authService.repositories.UserCredentialRepository;
import com.authService.dtos.RegisterRequest;
import com.authService.dtos.RegisterResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCredentialService implements UserDetailsService{

    private final UserCredentialRepository userCredentialRepository;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<RegisterResponse> registerUser(RegisterRequest request) {
        if (userCredentialRepository.findByUsername(request.getUsername()).isPresent()) {
            return new ResponseEntity<>(new RegisterResponse("Username already exists"), HttpStatus.BAD_REQUEST);
        }
        if (userCredentialRepository.findByEmail(request.getEmail()).isPresent()) {
            return new ResponseEntity<>(new RegisterResponse("Email already exists"), HttpStatus.BAD_REQUEST);
        }

        UserCredential userCredential = UserCredential.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(List.of("USER"))
                .build();

        userCredentialRepository.save(userCredential);
        return new ResponseEntity<>(new RegisterResponse("User registered successfully"), HttpStatus.CREATED);
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        return userCredentialRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail));
    }

}
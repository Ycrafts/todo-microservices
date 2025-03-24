package com.authService.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authService.dtos.RegisterRequest;
import com.authService.dtos.RegisterResponse;
import com.authService.services.UserCredentialService;

import lombok.RequiredArgsConstructor;;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserCredentialService userCredentialService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        return userCredentialService.registerUser(request);
    }
}
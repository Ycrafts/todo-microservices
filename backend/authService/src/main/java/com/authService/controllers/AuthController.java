package com.authService.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authService.dtos.AuthResponse;
import com.authService.dtos.LoginRequest;
import com.authService.dtos.RegisterRequest;
import com.authService.dtos.RegisterResponse;
import com.authService.services.UserCredentialService;
import com.authService.util.JwtUtil;

import lombok.RequiredArgsConstructor;;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class AuthController {

    private final UserCredentialService userCredentialService;

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    
    @PostMapping("users/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        return userCredentialService.registerUser(request);
    }

    @PostMapping("auth/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );

        if (authentication.isAuthenticated()) {
            UserDetails userDetails = userCredentialService.loadUserByUsername(request.getUsernameOrEmail());
            String jwtToken = jwtUtil.generateToken(userDetails);
            AuthResponse response = new AuthResponse();
            response.setToken(jwtToken);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).build();
        }
    }


    @GetMapping("test")
    public String test() {
        return "UserProfile service is running";
    }

}
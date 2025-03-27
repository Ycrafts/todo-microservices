package com.profileService.configs;



import com.profileService.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    // private final AuthenticationProvider authenticationProvider; // We might configure this later

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless JWT authentication
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Set session management to stateless
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/users/test").permitAll() // Allow public access to the test endpoint
                        .requestMatchers("/api/v1/users/**").authenticated() // Require authentication for all other /api/v1/users/** endpoints
                        .anyRequest().permitAll() // Or .denyAll() if you want to secure everything by default
                )
                // .authenticationProvider(authenticationProvider) // We might configure this later
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Add our JWT filter before the standard UsernamePasswordAuthenticationFilter

        return http.build();
    }

    // We might configure a basic AuthenticationProvider later if needed
    // @Bean
    // public AuthenticationProvider authenticationProvider() {
    //     // ... implementation ...
    // }
}
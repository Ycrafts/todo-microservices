package com.authService.authService;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // Activate the 'test' profile
class AuthServiceApplicationTests {

    @Test
    void contextLoads() {
        // This test checks if the application context starts successfully
    }
}
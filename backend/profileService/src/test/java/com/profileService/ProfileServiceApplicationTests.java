package com.profileService;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // Activate the 'test' profile
class ProfileServiceApplicationTests {

    @Test
    void contextLoads() {
        // This test checks if the application context starts successfully
    }
}
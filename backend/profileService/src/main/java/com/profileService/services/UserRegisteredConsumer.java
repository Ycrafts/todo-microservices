package com.profileService.services;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegisteredConsumer {

    private final UserProfileService userProfileService;

    @RabbitListener(queues = "${rabbitmq.queues.user-registered}")
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        userProfileService.createUserProfile(event.getUserId(), event.getUsername(), event.getEmail());
    }

    @lombok.Data
    private static class UserRegisteredEvent {
        private String userId;
        private String username;
        private String email;
    }
}
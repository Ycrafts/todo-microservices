package com.profileService.services;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.authService.events.UserRegisteredEvent;

@Service
@RequiredArgsConstructor
public class UserRegisteredConsumer {

    private final UserProfileService userProfileService;

    @RabbitListener(queues = "${rabbitmq.queue.user-registered}") 
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        // System.out.println("uuid from the event "+ event.getUserId());
        // System.out.println("the username from the event before creating the object:"+ event.getUsername());
        userProfileService.createUserProfile(event.getUserId(), event.getUsername(), event.getEmail());
    }
}
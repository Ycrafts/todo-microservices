package com.profileService.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.profileService.dtos.UserProfileResponse;
import com.profileService.dtos.UserProfileUpdateRequest;
import com.profileService.models.UserProfile;
import com.profileService.repositories.UserProfileRepository;

import com.todomicroservices.events.UserRegisteredEvent;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileResponse convertToUserProfileResponse(UserProfile userProfile) { // Changed to public
        UserProfileResponse response = new UserProfileResponse();
        response.setUsername(userProfile.getUsername());
        response.setEmail(userProfile.getEmail());
        return response;
    }

    public Optional<UserProfile> getUserProfileByAuthId(String authUserId) {
        return userProfileRepository.findByUserId(authUserId);
    }

    public List<UserProfileResponse> getAllUserProfiles() { // Potentially for admin use
        List<UserProfile> userProfiles = userProfileRepository.findAll();
        return userProfiles.stream()
                .map(this::convertToUserProfileResponse)
                .collect(Collectors.toList());
    }

    public Optional<UserProfile> updateUserProfile(String authUserId, UserProfileUpdateRequest updateRequest) {
        Optional<UserProfile> existingUserProfileOptional = userProfileRepository.findByUserId(authUserId);

        return existingUserProfileOptional.map(existingUserProfile -> {
            if (updateRequest.getEmail() != null) {
                if (userProfileRepository.findByEmail(updateRequest.getEmail()).isPresent() &&
                        !existingUserProfile.getEmail().equalsIgnoreCase(updateRequest.getEmail())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
                }
                existingUserProfile.setEmail(updateRequest.getEmail());
            }

            if (updateRequest.getUsername() != null) {
                if (userProfileRepository.findByUsername(updateRequest.getUsername()).isPresent() &&
                        !existingUserProfile.getUsername().equalsIgnoreCase(updateRequest.getUsername())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
                }
                existingUserProfile.setUsername(updateRequest.getUsername());
            }

            return userProfileRepository.save(existingUserProfile);
        });
    }

    public UserProfile createUserProfile(String authUserId, String username, String email) {
        UserProfile newUserProfile = UserProfile.builder()
                .userId(authUserId)
                .username(username)
                .email(email)
                .build();
        return userProfileRepository.save(newUserProfile);
    }
}
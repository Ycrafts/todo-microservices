package com.profileService.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.profileService.dtos.UserProfileCreationRequest;
import com.profileService.dtos.UserProfileResponse;
import com.profileService.dtos.UserProfileUpdateRequest;
import com.profileService.models.UserProfile;
import com.profileService.services.UserProfileService;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/{userId}") // For internal service communication (e.g., Task Management)
    @PreAuthorize("isAuthenticated()") // You might need a different authorization strategy here
    public ResponseEntity<UserProfileResponse> getUserProfileByUserId(@PathVariable String userId) {
        Optional<UserProfile> userProfileOptional = userProfileService.getUserProfileByAuthId(userId);
        return userProfileOptional.map(userProfile ->
                        new ResponseEntity<>(userProfileService.convertToUserProfileResponse(userProfile), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/") // Potentially for admin use to list all profiles
    @PreAuthorize("isAuthenticated() and hasRole('ADMIN')") // Adjust role as needed
    public ResponseEntity<List<UserProfileResponse>> getAllUserProfiles() {
        List<UserProfileResponse> userProfiles = userProfileService.getAllUserProfiles();
        return new ResponseEntity<>(userProfiles, HttpStatus.OK);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileResponse> getMyProfile(Principal principal) {
        Optional<UserProfile> userProfileOptional = userProfileService.getUserProfileByAuthId(principal.getName());
        return userProfileOptional.map(userProfile ->
                        new ResponseEntity<>(userProfileService.convertToUserProfileResponse(userProfile), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateMyProfile(Principal principal, @Valid @RequestBody UserProfileUpdateRequest updateRequest) {
        Optional<UserProfile> updatedUserProfile = userProfileService.updateUserProfile(principal.getName(), updateRequest);
        return updatedUserProfile.map(profile -> new ResponseEntity<>(HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Temporary endpoint for initial user profile creation by Auth Service
    @PostMapping
    public ResponseEntity<UserProfile> createUserProfile(@RequestBody UserProfileCreationRequest creationRequest) {
        UserProfile createdUser = userProfileService.createUserProfile(
                creationRequest.getUserId(),
                creationRequest.getUsername(),
                creationRequest.getEmail()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/test")
    public String test() {
        return "UserProfile service is running";
    }
}
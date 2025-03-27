package com.profileService.repositories;

import java.util.Optional;

import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.profileService.models.UserProfile;


@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUserId(String userId);
    Optional<UserProfile> findByEmail(String email);
    Optional<UserProfile> findByUsername(String username);
}
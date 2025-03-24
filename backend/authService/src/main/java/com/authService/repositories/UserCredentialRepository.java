package com.authService.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.authService.models.UserCredential;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredential, UUID> {
    Optional<UserCredential> findByUsername(String username);
    Optional<UserCredential> findByEmail(String email);
    Optional<UserCredential> findByUsernameOrEmail(String username, String email);
}
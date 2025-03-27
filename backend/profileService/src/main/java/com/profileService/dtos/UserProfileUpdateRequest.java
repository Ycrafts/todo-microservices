package com.profileService.dtos;

import lombok.Data;

@Data
public class UserProfileUpdateRequest {
    private String username;
    private String email;
}
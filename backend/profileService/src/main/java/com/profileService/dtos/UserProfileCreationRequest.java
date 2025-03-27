package com.profileService.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserProfileCreationRequest {
    private String userId; //from auth service
    private String username;
    private String email;
}   

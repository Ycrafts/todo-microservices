package com.authService.events;

import java.io.Serializable;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisteredEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String username;
    private String email;
    private Instant registrationTimestamp;


}
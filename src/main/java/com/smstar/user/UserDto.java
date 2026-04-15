package com.smstar.user;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String displayName;
    private String profilePicUrl;
    private String bio;
    private String role;
    private long coinBalance;
    private long diamondBalance;
    private LocalDateTime createdAt;
}

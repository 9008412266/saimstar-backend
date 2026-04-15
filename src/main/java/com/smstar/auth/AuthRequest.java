package com.smstar.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class AuthRequest {

    @Data
    public static class Register {
        @NotBlank
        @Size(min = 3, max = 50)
        private String username;

        @NotBlank
        @Email
        private String email;

        @NotBlank
        @Size(min = 6, max = 100)
        private String password;

        @Size(max = 100)
        private String displayName;
    }

    @Data
    public static class Login {
        @NotBlank
        @Email
        private String email;

        @NotBlank
        private String password;
    }

    @Data
    public static class RefreshToken {
        @NotBlank
        private String refreshToken;
    }
}

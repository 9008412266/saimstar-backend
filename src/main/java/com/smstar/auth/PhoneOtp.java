package com.smstar.auth;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "phone_otps")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class PhoneOtp {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 15)
    private String phone;

    @Column(name = "otp_code", nullable = false, length = 6)
    private String otpCode;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "is_used", nullable = false)
    private boolean isUsed = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

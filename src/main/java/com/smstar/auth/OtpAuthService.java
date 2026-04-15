package com.smstar.auth;

import com.smstar.user.User;
import com.smstar.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpAuthService {

    private final PhoneOtpRepository otpRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public Map<String, Object> sendOtp(String phone) {
        // Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(1000000));

        PhoneOtp phoneOtp = PhoneOtp.builder()
                .phone(phone)
                .otpCode(otp)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .isUsed(false)
                .createdAt(LocalDateTime.now())
                .build();
        otpRepository.save(phoneOtp);

        // In production: send SMS via Twilio/AWS SNS
        // For dev: return OTP in response
        return Map.of(
                "success", true,
                "message", "OTP sent to " + phone,
                "otp", otp,          // REMOVE in production!
                "expiresIn", 600     // seconds
        );
    }

    @Transactional
    public AuthResponse verifyOtp(String phone, String otp) {
        PhoneOtp phoneOtp = otpRepository
                .findTopByPhoneAndIsUsedFalseOrderByCreatedAtDesc(phone)
                .orElseThrow(() -> new RuntimeException("OTP not found or already used"));

        if (!phoneOtp.getOtpCode().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }
        if (phoneOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        phoneOtp.setUsed(true);
        otpRepository.save(phoneOtp);

        // Find or create user by phone
        User user = userRepository.findByPhoneNumber(phone).orElseGet(() -> {
            String username = "user_" + phone.replaceAll("[^0-9]", "").substring(Math.max(0, phone.length() - 6));
            return userRepository.save(User.builder()
                    .username(username + "_" + System.currentTimeMillis() % 10000)
                    .email(phone + "@phone.smstar.com")
                    .passwordHash("PHONE_AUTH")
                    .displayName(phone)
                    .phoneNumber(phone)
                    .authProvider(User.AuthProvider.phone)
                    .role(User.Role.USER)
                    .isActive(true)
                    .coinBalance(1000)
                    .diamondBalance(0)
                    .build());
        });

        String token   = jwtTokenProvider.generateTokenFromUsername(user.getEmail());
        String refresh = jwtTokenProvider.generateRefreshToken(user.getEmail());
        return AuthResponse.builder()
                .accessToken(token).refreshToken(refresh)
                .tokenType("Bearer").userId(user.getId())
                .username(user.getUsername()).email(user.getEmail())
                .role(user.getRole().name()).build();
    }

    @Transactional
    public AuthResponse socialLogin(String provider, String socialId, String email, String name) {
        User.AuthProvider authProvider = User.AuthProvider.valueOf(provider.toLowerCase());
        String lookupEmail = email != null ? email : socialId + "@" + provider + ".smstar.com";

        User user = userRepository.findByEmail(lookupEmail).orElseGet(() -> {
            String username = (name != null ? name.replaceAll("\\s+", "_").toLowerCase() : provider + "_user")
                    + "_" + System.currentTimeMillis() % 10000;
            return userRepository.save(User.builder()
                    .username(username)
                    .email(lookupEmail)
                    .passwordHash("SOCIAL_AUTH_" + provider.toUpperCase())
                    .displayName(name != null ? name : provider + " User")
                    .authProvider(authProvider)
                    .role(User.Role.USER)
                    .isActive(true)
                    .coinBalance(1000)
                    .diamondBalance(0)
                    .build());
        });

        String token   = jwtTokenProvider.generateTokenFromUsername(user.getEmail());
        String refresh = jwtTokenProvider.generateRefreshToken(user.getEmail());
        return AuthResponse.builder()
                .accessToken(token).refreshToken(refresh)
                .tokenType("Bearer").userId(user.getId())
                .username(user.getUsername()).email(user.getEmail())
                .role(user.getRole().name()).build();
    }
}

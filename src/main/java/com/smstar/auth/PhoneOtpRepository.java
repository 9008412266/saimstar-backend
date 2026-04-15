package com.smstar.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PhoneOtpRepository extends JpaRepository<PhoneOtp, Long> {
    Optional<PhoneOtp> findTopByPhoneAndIsUsedFalseOrderByCreatedAtDesc(String phone);
}

package HealthLink.HelathLink.repository;

import HealthLink.HelathLink.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {
    Optional<OtpVerification> findByEmailAndOtpAndUserTypeAndIsUsedFalse(String email, String otp, String userType);
    
    @Modifying
    @Query("DELETE FROM OtpVerification o WHERE o.expiresAt < :now")
    void deleteExpiredOtps(@Param("now") LocalDateTime now);
    
    Optional<OtpVerification> findTopByEmailAndUserTypeAndIsUsedFalseOrderByCreatedAtDesc(String email, String userType);
}


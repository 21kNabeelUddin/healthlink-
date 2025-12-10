package com.healthlink.service.auth;

import com.healthlink.infrastructure.logging.SafeLogger;
import com.healthlink.service.notification.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import jakarta.annotation.PostConstruct;

/**
 * OTP Service for email-based OTP authentication
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final RedisTemplate<String, String> redisTemplate;
    private final EmailService emailService;
    private static final String OTP_PREFIX = "otp:";
    private static final String OTP_ATTEMPTS_PREFIX = "otp_attempts:";
    private static final int OTP_LENGTH = 6;
    private static final long OTP_EXPIRATION_MINUTES = 5;
    private static final int MAX_OTP_ATTEMPTS = 5;
    private static final SecureRandom random = new SecureRandom();
    
    // In-memory OTP storage for dev mode (when Redis is disabled)
    private final ConcurrentHashMap<String, OtpEntry> inMemoryOtps = new ConcurrentHashMap<>();
    
    private static class OtpEntry {
        final String otp;
        final Instant expiresAt;
        
        OtpEntry(String otp, Instant expiresAt) {
            this.otp = otp;
            this.expiresAt = expiresAt;
        }
        
        boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }

    @Value("${healthlink.otp.redis-enabled:false}")
    private boolean redisEnabled;

    @Value("${healthlink.otp.email-enabled:false}")
    private boolean emailEnabled;

    @PostConstruct
    public void logConfiguration() {
        SafeLogger.get(OtpService.class)
                .event("otp_service_initialized")
                .with("redis_enabled", redisEnabled)
                .with("email_enabled", emailEnabled)
                .log();
        if (!emailEnabled) {
            log.warn("⚠️ OTP EMAIL IS DISABLED! Set HEALTHLINK_OTP_EMAIL_ENABLED=true to enable email sending.");
        }
        if (!redisEnabled) {
            log.info("OTP Service running in dev mode (in-memory storage). OTPs will be validated properly.");
        }
    }
    
    /**
     * Clean up expired OTPs from in-memory storage every 5 minutes
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void cleanupExpiredOtps() {
        if (!redisEnabled) {
            int removed = 0;
            for (String email : inMemoryOtps.keySet()) {
                OtpEntry entry = inMemoryOtps.get(email);
                if (entry != null && entry.isExpired()) {
                    inMemoryOtps.remove(email);
                    removed++;
                }
            }
            if (removed > 0) {
                log.debug("Cleaned up {} expired OTPs from memory", removed);
            }
        }
    }

    /**
     * Generate and store OTP for email
     */
    public String generateOtp(String email) {
        String otp = String.format("%0" + OTP_LENGTH + "d", random.nextInt((int) Math.pow(10, OTP_LENGTH)));

        // If Redis-backed OTP is disabled, store in memory for dev mode
        if (!redisEnabled) {
            // Store OTP in memory with expiration
            Instant expiresAt = Instant.now().plus(Duration.ofMinutes(OTP_EXPIRATION_MINUTES));
            inMemoryOtps.put(email, new OtpEntry(otp, expiresAt));
            
            SafeLogger.get(OtpService.class)
                    .event("otp_generated_dev_mode")
                    .withMasked("email", email)
                    .with("otp", otp)
                    .log();
            sendOtpEmailIfEnabled(email, otp);
            return otp;
        }

        // Check rate limiting
        String attemptsKey = OTP_ATTEMPTS_PREFIX + email;
        String attempts;
        try {
            attempts = redisTemplate.opsForValue().get(attemptsKey);
        } catch (RedisConnectionFailureException ex) {
            SafeLogger.get(OtpService.class)
                    .event("otp_redis_unavailable")
                    .withMasked("email", email)
                    .with("error", ex.getMessage())
                    .log();
            // Fallback to dev behavior when Redis is unavailable
            sendOtpEmailIfEnabled(email, otp);
            return otp;
        } catch (DataAccessException ex) {
            SafeLogger.get(OtpService.class)
                    .event("otp_redis_unavailable")
                    .withMasked("email", email)
                    .with("error", ex.getMessage())
                    .log();
            // Fallback to dev behavior when Redis is unavailable
            sendOtpEmailIfEnabled(email, otp);
            return otp;
        }

        int attemptCount = 0;
        try {
            attemptCount = attempts != null ? Integer.parseInt(attempts) : 0;
        } catch (NumberFormatException e) {
            SafeLogger.get(OtpService.class)
                .event("invalid_attempt_count")
                .withMasked("email", email)
                .log();
            redisTemplate.delete(attemptsKey);
        }

        if (attemptCount >= MAX_OTP_ATTEMPTS) {
            throw new RuntimeException("Too many OTP requests. Please try again later.");
        }

        // Store in Redis with expiration
        String key = OTP_PREFIX + email;
        redisTemplate.opsForValue().set(key, otp, OTP_EXPIRATION_MINUTES, TimeUnit.MINUTES);

        // Increment attempts counter (expires in 1 hour)
        redisTemplate.opsForValue().increment(attemptsKey);
        redisTemplate.expire(attemptsKey, Duration.ofHours(1));

        SafeLogger.get(OtpService.class)
            .event("otp_generated")
            .withMasked("email", email)
            .log();
        sendOtpEmailIfEnabled(email, otp);
        return otp;
    }

    /**
     * Verify OTP for email
     */
    public boolean verifyOtp(String email, String otp) {
        // In dev mode (Redis disabled), verify against in-memory storage
        if (!redisEnabled) {
            OtpEntry entry = inMemoryOtps.get(email);
            
            if (entry == null) {
                SafeLogger.get(OtpService.class)
                        .event("otp_verification_failed_dev_mode")
                        .withMasked("email", email)
                        .with("reason", "otp_not_found")
                        .log();
                log.warn("OTP verification failed for {}: OTP not found in memory", email);
                return false;
            }
            
            if (entry.isExpired()) {
                inMemoryOtps.remove(email);
                SafeLogger.get(OtpService.class)
                        .event("otp_verification_failed_dev_mode")
                        .withMasked("email", email)
                        .with("reason", "otp_expired")
                        .log();
                log.warn("OTP verification failed for {}: OTP expired", email);
                return false;
            }
            
            if (!entry.otp.equals(otp)) {
                SafeLogger.get(OtpService.class)
                        .event("otp_verification_failed_dev_mode")
                        .withMasked("email", email)
                        .with("reason", "otp_mismatch")
                        .log();
                log.warn("OTP verification failed for {}: OTP mismatch (expected: {}, provided: {})", email, entry.otp, otp);
                return false;
            }
            
            // OTP verified successfully - remove it
            inMemoryOtps.remove(email);
            SafeLogger.get(OtpService.class)
                    .event("otp_verified_dev_mode")
                    .withMasked("email", email)
                    .log();
            log.info("OTP verified successfully for: {}", email);
            return true;
        }

        String key = OTP_PREFIX + email;
        String storedOtp;
        try {
            storedOtp = redisTemplate.opsForValue().get(key);
        } catch (RedisConnectionFailureException ex) {
            // Redis is down/unreachable – treat as verification failure instead of throwing
            SafeLogger.get(OtpService.class)
                    .event("otp_redis_unavailable_verify")
                    .withMasked("email", email)
                    .with("error", ex.getMessage())
                    .log();
            return false;
        } catch (DataAccessException ex) {
            SafeLogger.get(OtpService.class)
                    .event("otp_redis_unavailable_verify")
                    .withMasked("email", email)
                    .with("error", ex.getMessage())
                    .log();
            return false;
        }

        if (storedOtp == null) {
            SafeLogger.get(OtpService.class)
                .event("otp_not_found")
                .withMasked("email", email)
                .log();
            return false;
        }

        if (storedOtp.equals(otp)) {
            // OTP verified, delete it
            redisTemplate.delete(key);
            SafeLogger.get(OtpService.class)
                .event("otp_verified")
                .withMasked("email", email)
                .log();
            return true;
        }

        SafeLogger.get(OtpService.class)
            .event("otp_invalid")
            .withMasked("email", email)
            .log();
        return false;
    }

    /**
     * Delete OTP (if needed to cancel)
     */
    public void deleteOtp(String email) {
        if (!redisEnabled) {
            // Nothing to delete in dev mode
            return;
        }

        String key = OTP_PREFIX + email;
        try {
            redisTemplate.delete(key);
        } catch (RedisConnectionFailureException ex) {
            SafeLogger.get(OtpService.class)
                    .event("otp_redis_unavailable_delete")
                    .withMasked("email", email)
                    .with("error", ex.getMessage())
                    .log();
        } catch (DataAccessException ex) {
            SafeLogger.get(OtpService.class)
                    .event("otp_redis_unavailable_delete")
                    .withMasked("email", email)
                    .with("error", ex.getMessage())
                    .log();
        }
        SafeLogger.get(OtpService.class)
            .event("otp_deleted")
            .withMasked("email", email)
            .log();
    }

    /**
     * Check if OTP exists for email
     */
    public boolean otpExists(String email) {
        if (!redisEnabled) {
            // Check in-memory storage for dev mode
            OtpEntry entry = inMemoryOtps.get(email);
            return entry != null && !entry.isExpired();
        }

        String key = OTP_PREFIX + email;
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (RedisConnectionFailureException ex) {
            SafeLogger.get(OtpService.class)
                    .event("otp_redis_unavailable_exists")
                    .withMasked("email", email)
                    .with("error", ex.getMessage())
                    .log();
            return false;
        } catch (DataAccessException ex) {
            SafeLogger.get(OtpService.class)
                    .event("otp_redis_unavailable_exists")
                    .withMasked("email", email)
                    .with("error", ex.getMessage())
                    .log();
            return false;
        }
    }

    private void sendOtpEmailIfEnabled(String email, String otp) {
        if (!emailEnabled) {
            SafeLogger.get(OtpService.class)
                    .event("otp_email_skipped_disabled")
                    .withMasked("email", email)
                    .with("otp", otp)
                    .log();
            log.warn("OTP email skipped - email is disabled. OTP: {}", otp);
            return;
        }
        
        SafeLogger.get(OtpService.class)
                .event("otp_email_attempting")
                .withMasked("email", email)
                .log();
        log.info("Attempting to send OTP email to: {}", email);
        
        try {
            // Send email synchronously for registration to ensure it only happens after successful save
            // For other cases, async is fine, but for registration we want to ensure transaction success
            emailService.sendSimpleEmailSync(
                    email,
                    "Your HealthLink verification code",
                    "Your one-time verification code is: " + otp + "\n\n" +
                            "This code will expire in " + OTP_EXPIRATION_MINUTES + " minutes.");
            
            SafeLogger.get(OtpService.class)
                    .event("otp_email_sent_success")
                    .withMasked("email", email)
                    .log();
            log.info("OTP email sent successfully to: {}", email);
        } catch (Exception ex) {
            // Log OTP in logs so it can be retrieved if email fails (common in cloud platforms)
            SafeLogger.get(OtpService.class)
                    .event("otp_email_send_failed")
                    .withMasked("email", email)
                    .with("otp", otp)  // Log OTP so it can be retrieved from logs if email fails
                    .with("error", ex.getMessage())
                    .with("error_class", ex.getClass().getName())
                    .log();
            log.error("❌ Failed to send OTP email to: {} - Error: {}", email, ex.getMessage(), ex);
            log.error("⚠️ OTP for {} is: {} (check logs if email delivery failed)", email, otp);
            log.error("⚠️ This is common on cloud platforms (Railway, Render, etc.) that block SMTP connections.");
            log.error("⚠️ Consider using SendGrid, Mailgun, or AWS SES instead of Gmail SMTP for production.");
            // Don't throw - email failure shouldn't break registration, but log OTP so it can be retrieved
        }
    }
}

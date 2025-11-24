package HealthLink.HelathLink.service;

import HealthLink.HelathLink.entity.OtpVerification;
import HealthLink.HelathLink.repository.OtpVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final JavaMailSender mailSender;
    private final OtpVerificationRepository otpRepository;

    @Value("${otp.expiration.minutes:10}")
    private int otpExpirationMinutes;

    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // 6-digit OTP
        return String.valueOf(otp);
    }

    public void sendOtpEmail(String email, String otp, String userType) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("HealthLink+ - OTP Verification");
        message.setText("Dear " + userType + ",\n\n" +
                "Your OTP for account verification is: " + otp + "\n\n" +
                "This OTP will expire in " + otpExpirationMinutes + " minutes.\n\n" +
                "If you did not request this, please ignore this email.\n\n" +
                "Best regards,\nHealthLink+ Team");
        mailSender.send(message);
    }

    public OtpVerification saveOtp(String email, String otp, String userType) {
        // Delete any existing unused OTPs for this email and user type
        Optional<OtpVerification> existingOtp = otpRepository.findTopByEmailAndUserTypeAndIsUsedFalseOrderByCreatedAtDesc(email, userType);
        existingOtp.ifPresent(otpRepository::delete);

        OtpVerification otpVerification = new OtpVerification();
        otpVerification.setEmail(email);
        otpVerification.setOtp(otp);
        otpVerification.setUserType(userType);
        otpVerification.setIsUsed(false);
        otpVerification.setExpiresAt(LocalDateTime.now().plusMinutes(otpExpirationMinutes));
        return otpRepository.save(otpVerification);
    }

    public boolean verifyOtp(String email, String otp, String userType) {
        Optional<OtpVerification> otpVerification = otpRepository
                .findByEmailAndOtpAndUserTypeAndIsUsedFalse(email, otp, userType);

        if (otpVerification.isPresent()) {
            OtpVerification otpEntity = otpVerification.get();
            if (otpEntity.getExpiresAt().isAfter(LocalDateTime.now())) {
                otpEntity.setIsUsed(true);
                otpRepository.save(otpEntity);
                return true;
            }
        }
        return false;
    }
}


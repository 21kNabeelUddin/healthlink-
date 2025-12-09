package com.healthlink.service.notification;

import com.healthlink.infrastructure.logging.SafeLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.annotation.PostConstruct;
import java.util.Map;

/**
 * Email Service with template support
 * Handles all email notifications for the platform
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Autowired
    private Environment environment;

    @Value("${healthlink.mail.from:noreply@healthlink.com}")
    private String fromEmail;

    @Value("${healthlink.mail.from-name:HealthLink Platform}")
    private String fromName;

    @PostConstruct
    public void logEmailConfiguration() {
        String mailHost = environment.getProperty("spring.mail.host", "NOT SET");
        String mailPort = environment.getProperty("spring.mail.port", "NOT SET");
        String mailUsername = environment.getProperty("spring.mail.username", "NOT SET");
        boolean mailAuth = environment.getProperty("spring.mail.properties.mail.smtp.auth", Boolean.class, false);
        boolean starttls = environment.getProperty("spring.mail.properties.mail.smtp.starttls.enable", Boolean.class, false);
        
        log.info("📧 Email Service Configuration:");
        log.info("   Host: {}", mailHost);
        log.info("   Port: {}", mailPort);
        log.info("   Username: {}", mailUsername.isEmpty() ? "NOT SET" : mailUsername);
        log.info("   Auth: {}", mailAuth);
        log.info("   STARTTLS: {}", starttls);
        log.info("   From: {}", fromEmail);
        log.info("   From Name: {}", fromName);
        
        if (mailHost.equals("NOT SET") || mailHost.equals("localhost")) {
            log.error("❌ MAIL_HOST is not properly configured! Current value: {}", mailHost);
        }
        if (mailUsername.equals("NOT SET") || mailUsername.isEmpty()) {
            log.error("❌ MAIL_USERNAME is not set!");
        }
    }

    /**
     * Send password reset email with OTP
     */
    @Async
    public void sendPasswordResetEmail(String toEmail, String userName, String otp) {
        try {
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("otp", otp);
            context.setVariable("validityMinutes", 15);

            String htmlContent = templateEngine.process("email/password-reset", context);

            sendHtmlEmail(
                    toEmail,
                    "Reset Your Password - HealthLink",
                    htmlContent);

            SafeLogger.get(EmailService.class)
                .event("email_sent")
                .with("template", "password-reset")
                .withMasked("email", toEmail)
                .log();
        } catch (Exception e) {
            SafeLogger.get(EmailService.class)
                .event("email_send_failed")
                .with("template", "password-reset")
                .withMasked("email", toEmail)
                .with("error", e.getClass().getSimpleName())
                .log();
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    /**
     * Send password reset confirmation email
     */
    @Async
    public void sendPasswordResetConfirmation(String toEmail, String userName) {
        try {
            Context context = new Context();
            context.setVariable("userName", userName);

            String htmlContent = templateEngine.process("email/password-reset-confirmation", context);

            sendHtmlEmail(
                    toEmail,
                    "Password Reset Successful - HealthLink",
                    htmlContent);

            SafeLogger.get(EmailService.class)
                .event("email_sent")
                .with("template", "password-reset-confirmation")
                .withMasked("email", toEmail)
                .log();
        } catch (Exception e) {
            SafeLogger.get(EmailService.class)
                .event("email_send_failed")
                .with("template", "password-reset-confirmation")
                .withMasked("email", toEmail)
                .log();
        }
    }

    /**
     * Send account approval email
     */
    @Async
    public void sendAccountApprovalEmail(String toEmail, String userName, String role) {
        try {
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("role", role);

            String htmlContent = templateEngine.process("email/account-approved", context);

            sendHtmlEmail(
                    toEmail,
                    "Account Approved - Welcome to HealthLink",
                    htmlContent);

            SafeLogger.get(EmailService.class)
                .event("email_sent")
                .with("template", "account-approved")
                .withMasked("email", toEmail)
                .log();
        } catch (Exception e) {
            SafeLogger.get(EmailService.class)
                .event("email_send_failed")
                .with("template", "account-approved")
                .withMasked("email", toEmail)
                .log();
        }
    }

    /**
     * Send account rejection email
     */
    @Async
    public void sendAccountRejectionEmail(String toEmail, String userName, String role, String reason) {
        try {
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("role", role);
            context.setVariable("reason", reason);

            String htmlContent = templateEngine.process("email/account-rejected", context);

            sendHtmlEmail(
                    toEmail,
                    "Account Application Update - HealthLink",
                    htmlContent);

            SafeLogger.get(EmailService.class)
                .event("email_sent")
                .with("template", "account-rejected")
                .withMasked("email", toEmail)
                .log();
        } catch (Exception e) {
            SafeLogger.get(EmailService.class)
                .event("email_send_failed")
                .with("template", "account-rejected")
                .withMasked("email", toEmail)
                .log();
        }
    }

    /**
     * Send appointment confirmation email
     */
    @Async
    public void sendAppointmentConfirmation(String toEmail, String patientName, String doctorName,
            String appointmentTime) {
        try {
            Context context = new Context();
            context.setVariable("patientName", patientName);
            context.setVariable("doctorName", doctorName);
            context.setVariable("appointmentTime", appointmentTime);

            String htmlContent = templateEngine.process("email/appointment-confirmation", context);

            sendHtmlEmail(
                    toEmail,
                    "Appointment Confirmed - HealthLink",
                    htmlContent);

            SafeLogger.get(EmailService.class)
                .event("email_sent")
                .with("template", "appointment-confirmation")
                .withMasked("email", toEmail)
                .log();
        } catch (Exception e) {
            SafeLogger.get(EmailService.class)
                .event("email_send_failed")
                .with("template", "appointment-confirmation")
                .withMasked("email", toEmail)
                .log();
        }
    }

    /**
     * Send appointment reminder email
     */
    @Async
    public void sendAppointmentReminder(String toEmail, String patientName, String doctorName, String appointmentTime) {
        try {
            Context context = new Context();
            context.setVariable("patientName", patientName);
            context.setVariable("doctorName", doctorName);
            context.setVariable("appointmentTime", appointmentTime);

            String htmlContent = templateEngine.process("email/appointment-reminder", context);

            sendHtmlEmail(
                    toEmail,
                    "Appointment Reminder - Tomorrow - HealthLink",
                    htmlContent);

            SafeLogger.get(EmailService.class)
                .event("email_sent")
                .with("template", "appointment-reminder")
                .withMasked("email", toEmail)
                .log();
        } catch (Exception e) {
            SafeLogger.get(EmailService.class)
                .event("email_send_failed")
                .with("template", "appointment-reminder")
                .withMasked("email", toEmail)
                .log();
        }
    }

    /**
     * Send welcome email to emergency patient with password reset instructions
     */
    @Async
    public void sendEmergencyPatientWelcomeEmail(String toEmail, String patientName) {
        try {
            Context context = new Context();
            context.setVariable("patientName", patientName);
            context.setVariable("loginUrl", "http://localhost:3000/auth/patient/login"); // TODO: Make this configurable

            String htmlContent = templateEngine.process("email/emergency-patient-welcome", context);

            sendHtmlEmail(
                    toEmail,
                    "Welcome to HealthLink - Account Created",
                    htmlContent);

            SafeLogger.get(EmailService.class)
                .event("email_sent")
                .with("template", "emergency-patient-welcome")
                .withMasked("email", toEmail)
                .log();
        } catch (Exception e) {
            SafeLogger.get(EmailService.class)
                .event("email_send_failed")
                .with("template", "emergency-patient-welcome")
                .withMasked("email", toEmail)
                .with("error", e.getClass().getSimpleName())
                .log();
            // Don't throw - email failure shouldn't break patient creation
        }
    }

    /**
     * Send payment verification notification to staff
     */
    @Async
    public void sendPaymentVerificationNotification(String toEmail, String staffName, String patientName,
            String amount) {
        try {
            Context context = new Context();
            context.setVariable("staffName", staffName);
            context.setVariable("patientName", patientName);
            context.setVariable("amount", amount);

            String htmlContent = templateEngine.process("email/payment-verification-needed", context);

            sendHtmlEmail(
                    toEmail,
                    "Payment Verification Required - HealthLink",
                    htmlContent);

            SafeLogger.get(EmailService.class)
                .event("email_sent")
                .with("template", "payment-verification-needed")
                .withMasked("email", toEmail)
                .log();
        } catch (Exception e) {
            SafeLogger.get(EmailService.class)
                .event("email_send_failed")
                .with("template", "payment-verification-needed")
                .withMasked("email", toEmail)
                .log();
        }
    }

    /**
     * Send payment verified confirmation to patient
     */
    @Async
    public void sendPaymentVerifiedEmail(String toEmail, String patientName, String amount, String appointmentDetails) {
        try {
            Context context = new Context();
            context.setVariable("patientName", patientName);
            context.setVariable("amount", amount);
            context.setVariable("appointmentDetails", appointmentDetails);

            String htmlContent = templateEngine.process("email/payment-verified", context);

            sendHtmlEmail(
                    toEmail,
                    "Payment Verified - HealthLink",
                    htmlContent);

            SafeLogger.get(EmailService.class)
                .event("email_sent")
                .with("template", "payment-verified")
                .withMasked("email", toEmail)
                .log();
        } catch (Exception e) {
            SafeLogger.get(EmailService.class)
                .event("email_send_failed")
                .with("template", "payment-verified")
                .withMasked("email", toEmail)
                .log();
        }
    }

    /**
     * Send generic email with template
     */
    @Async
    public void sendTemplatedEmail(String toEmail, String subject, String templateName, Map<String, Object> variables) {
        try {
            Context context = new Context();
            context.setVariables(variables);

            String htmlContent = templateEngine.process("email/" + templateName, context);

            sendHtmlEmail(toEmail, subject, htmlContent);

            SafeLogger.get(EmailService.class)
                .event("email_sent")
                .with("template", templateName)
                .withMasked("email", toEmail)
                .log();
        } catch (Exception e) {
            SafeLogger.get(EmailService.class)
                .event("email_send_failed")
                .with("template", templateName)
                .withMasked("email", toEmail)
                .log();
        }
    }

    /**
     * Send simple text email
     */
    @Async
    public void sendSimpleEmail(String toEmail, String subject, String body) {
        log.info("📧 Attempting to send email to: {} with subject: {}", toEmail, subject);
        log.info("📧 Email from: {}", fromEmail);
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);

            log.info("📧 Sending email via mailSender...");
            mailSender.send(message);
            log.info("✅ Email sent successfully to: {}", toEmail);

            SafeLogger.get(EmailService.class)
                .event("email_sent")
                .with("type", "simple")
                .withMasked("email", toEmail)
                .log();
        } catch (Exception e) {
            log.error("❌ Failed to send email to: {} - Error: {}", toEmail, e.getMessage(), e);
            log.error("❌ Exception type: {}", e.getClass().getName());
            if (e.getCause() != null) {
                log.error("❌ Caused by: {}", e.getCause().getMessage());
            }
            
            SafeLogger.get(EmailService.class)
                .event("email_send_failed")
                .with("type", "simple")
                .withMasked("email", toEmail)
                .with("error", e.getMessage())
                .with("error_class", e.getClass().getName())
                .log();
            
            // Re-throw so caller knows it failed
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    /**
     * Send HTML email
     */
    private void sendHtmlEmail(String toEmail, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        try {
            helper.setFrom(fromEmail, fromName);
        } catch (java.io.UnsupportedEncodingException e) {
            log.error("Invalid email encoding", e);
            throw new RuntimeException("Failed to set email sender", e);
        }
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}

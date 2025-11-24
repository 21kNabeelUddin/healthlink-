package HealthLink.HelathLink.service;

import HealthLink.HelathLink.dto.*;
import HealthLink.HelathLink.entity.Patient;
import HealthLink.HelathLink.repository.PatientRepository;
import HealthLink.HelathLink.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final OtpService otpService;

    public ApiResponseDTO<String> signup(SignupRequestDTO signupRequest) {
        // Check if email already exists
        Patient existingPatient = patientRepository.findByEmail(signupRequest.getEmail()).orElse(null);
        
        if (existingPatient != null) {
            // If account exists but is not verified, allow resending OTP
            if (!existingPatient.getIsVerified()) {
                // Generate and resend OTP for unverified account
                String otp = otpService.generateOtp();
                otpService.saveOtp(signupRequest.getEmail(), otp, "PATIENT");
                
                try {
                    otpService.sendOtpEmail(signupRequest.getEmail(), otp, "Patient");
                    return ApiResponseDTO.success("OTP resent successfully. Please verify your email with OTP.", null);
                } catch (Exception e) {
                    System.err.println("Failed to send OTP email: " + e.getMessage());
                    System.out.println("========================================");
                    System.out.println("DEVELOPMENT MODE - OTP for " + signupRequest.getEmail() + ": " + otp);
                    System.out.println("========================================");
                    return ApiResponseDTO.success("OTP resent successfully. OTP email could not be sent. Please contact support.", null);
                }
            } else {
                return ApiResponseDTO.error("Email already registered");
            }
        }

        // Check if phone number already exists (only for new accounts)
        if (patientRepository.existsByPhoneNumber(signupRequest.getPhoneNumber())) {
            return ApiResponseDTO.error("Phone number already registered");
        }

        // Validate patient-specific fields
        if (signupRequest.getDateOfBirth() == null || signupRequest.getAddress() == null) {
            return ApiResponseDTO.error("Date of birth and address are required for patient registration");
        }

        // Create patient entity
        Patient patient = new Patient();
        patient.setEmail(signupRequest.getEmail());
        patient.setPassword(PasswordUtil.hashPassword(signupRequest.getPassword()));
        patient.setFirstName(signupRequest.getFirstName());
        patient.setLastName(signupRequest.getLastName());
        patient.setPhoneNumber(signupRequest.getPhoneNumber());
        patient.setDateOfBirth(signupRequest.getDateOfBirth());
        patient.setAddress(signupRequest.getAddress());
        patient.setIsVerified(false);

        patientRepository.save(patient);

        // Generate and send OTP
        String otp = otpService.generateOtp();
        otpService.saveOtp(signupRequest.getEmail(), otp, "PATIENT");
        
        try {
            otpService.sendOtpEmail(signupRequest.getEmail(), otp, "Patient");
            return ApiResponseDTO.success("Patient registered successfully. Please verify your email with OTP.", null);
        } catch (Exception e) {
            // If email sending fails, still return success but log the OTP for development
            // OTP is already saved in database
            System.err.println("Failed to send OTP email: " + e.getMessage());
            System.out.println("========================================");
            System.out.println("DEVELOPMENT MODE - OTP for " + signupRequest.getEmail() + ": " + otp);
            System.out.println("========================================");
            return ApiResponseDTO.success("Patient registered successfully. OTP email could not be sent. Please contact support.", null);
        }
    }

    @Transactional
    public ApiResponseDTO<UserResponseDTO> verifyOtp(OtpVerificationRequestDTO otpRequest) {
        if (!otpRequest.getUserType().equals("PATIENT")) {
            return ApiResponseDTO.error("Invalid user type for patient verification");
        }

        boolean isValid = otpService.verifyOtp(otpRequest.getEmail(), otpRequest.getOtp(), "PATIENT");
        if (!isValid) {
            return ApiResponseDTO.error("Invalid or expired OTP");
        }

        Patient patient = patientRepository.findByEmail(otpRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        patient.setIsVerified(true);
        patientRepository.save(patient);

        UserResponseDTO userResponse = mapToUserResponseDTO(patient);
        return ApiResponseDTO.success("Email verified successfully", userResponse);
    }

    public ApiResponseDTO<LoginResponseDTO> login(LoginRequestDTO loginRequest) {
        Patient patient = patientRepository.findByEmail(loginRequest.getEmail())
                .orElse(null);

        if (patient == null) {
            return ApiResponseDTO.error("Invalid email or password");
        }

        if (!PasswordUtil.verifyPassword(loginRequest.getPassword(), patient.getPassword())) {
            return ApiResponseDTO.error("Invalid email or password");
        }

        if (!patient.getIsVerified()) {
            return ApiResponseDTO.error("Please verify your email first");
        }

        UserResponseDTO userResponse = mapToUserResponseDTO(patient);
        LoginResponseDTO loginResponse = new LoginResponseDTO();
        loginResponse.setMessage("Login successful");
        loginResponse.setUser(userResponse);
        loginResponse.setToken(""); // For future JWT implementation

        return ApiResponseDTO.success("Login successful", loginResponse);
    }

    private UserResponseDTO mapToUserResponseDTO(Patient patient) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(patient.getId());
        dto.setEmail(patient.getEmail());
        dto.setFirstName(patient.getFirstName());
        dto.setLastName(patient.getLastName());
        dto.setPhoneNumber(patient.getPhoneNumber());
        dto.setUserType("PATIENT");
        dto.setIsVerified(patient.getIsVerified());
        dto.setCreatedAt(patient.getCreatedAt());
        dto.setDateOfBirth(patient.getDateOfBirth());
        dto.setAddress(patient.getAddress());
        return dto;
    }
}


package HealthLink.HelathLink.service;

import HealthLink.HelathLink.dto.*;
import HealthLink.HelathLink.entity.Doctor;
import HealthLink.HelathLink.repository.DoctorRepository;
import HealthLink.HelathLink.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final OtpService otpService;

    public ApiResponseDTO<String> signup(SignupRequestDTO signupRequest) {
        // Check if email already exists
        Doctor existingDoctor = doctorRepository.findByEmail(signupRequest.getEmail()).orElse(null);
        
        if (existingDoctor != null) {
            // If account exists but is not verified, allow resending OTP
            if (!existingDoctor.getIsVerified()) {
                // Generate and resend OTP for unverified account
                String otp = otpService.generateOtp();
                otpService.saveOtp(signupRequest.getEmail(), otp, "DOCTOR");
                
                try {
                    otpService.sendOtpEmail(signupRequest.getEmail(), otp, "Doctor");
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
        if (doctorRepository.existsByPhoneNumber(signupRequest.getPhoneNumber())) {
            return ApiResponseDTO.error("Phone number already registered");
        }

        // Validate doctor-specific fields
        if (signupRequest.getSpecialization() == null || 
            signupRequest.getLicenseNumber() == null || 
            signupRequest.getYearsOfExperience() == null) {
            return ApiResponseDTO.error("Specialization, license number, and years of experience are required for doctor registration");
        }

        // Create doctor entity
        Doctor doctor = new Doctor();
        doctor.setEmail(signupRequest.getEmail());
        doctor.setPassword(PasswordUtil.hashPassword(signupRequest.getPassword()));
        doctor.setFirstName(signupRequest.getFirstName());
        doctor.setLastName(signupRequest.getLastName());
        doctor.setPhoneNumber(signupRequest.getPhoneNumber());
        doctor.setSpecialization(signupRequest.getSpecialization());
        doctor.setLicenseNumber(signupRequest.getLicenseNumber());
        doctor.setYearsOfExperience(signupRequest.getYearsOfExperience());
        doctor.setIsVerified(false);

        doctorRepository.save(doctor);

        // Generate and send OTP
        String otp = otpService.generateOtp();
        otpService.saveOtp(signupRequest.getEmail(), otp, "DOCTOR");
        
        try {
            otpService.sendOtpEmail(signupRequest.getEmail(), otp, "Doctor");
            return ApiResponseDTO.success("Doctor registered successfully. Please verify your email with OTP.", null);
        } catch (Exception e) {
            // If email sending fails, still return success but log the OTP for development
            System.err.println("Failed to send OTP email: " + e.getMessage());
            System.out.println("========================================");
            System.out.println("DEVELOPMENT MODE - OTP for " + signupRequest.getEmail() + ": " + otp);
            System.out.println("========================================");
            return ApiResponseDTO.success("Doctor registered successfully. OTP email could not be sent. Please contact support.", null);
        }
    }

    @Transactional
    public ApiResponseDTO<UserResponseDTO> verifyOtp(OtpVerificationRequestDTO otpRequest) {
        if (!otpRequest.getUserType().equals("DOCTOR")) {
            return ApiResponseDTO.error("Invalid user type for doctor verification");
        }

        boolean isValid = otpService.verifyOtp(otpRequest.getEmail(), otpRequest.getOtp(), "DOCTOR");
        if (!isValid) {
            return ApiResponseDTO.error("Invalid or expired OTP");
        }

        Doctor doctor = doctorRepository.findByEmail(otpRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        doctor.setIsVerified(true);
        doctorRepository.save(doctor);

        UserResponseDTO userResponse = mapToUserResponseDTO(doctor);
        return ApiResponseDTO.success("Email verified successfully", userResponse);
    }

    public ApiResponseDTO<LoginResponseDTO> login(LoginRequestDTO loginRequest) {
        Doctor doctor = doctorRepository.findByEmail(loginRequest.getEmail())
                .orElse(null);

        if (doctor == null) {
            return ApiResponseDTO.error("Invalid email or password");
        }

        if (!PasswordUtil.verifyPassword(loginRequest.getPassword(), doctor.getPassword())) {
            return ApiResponseDTO.error("Invalid email or password");
        }

        if (!doctor.getIsVerified()) {
            return ApiResponseDTO.error("Please verify your email first");
        }

        UserResponseDTO userResponse = mapToUserResponseDTO(doctor);
        LoginResponseDTO loginResponse = new LoginResponseDTO();
        loginResponse.setMessage("Login successful");
        loginResponse.setUser(userResponse);
        loginResponse.setToken(""); // For future JWT implementation

        return ApiResponseDTO.success("Login successful", loginResponse);
    }

    public ApiResponseDTO<java.util.List<UserResponseDTO>> getAllVerifiedDoctors(String specialization) {
        java.util.List<Doctor> doctors;
        if (specialization != null && !specialization.trim().isEmpty()) {
            doctors = doctorRepository.findByIsVerifiedTrueAndSpecializationIgnoreCase(specialization);
        } else {
            doctors = doctorRepository.findByIsVerifiedTrue();
        }
        
        java.util.List<UserResponseDTO> response = doctors.stream()
                .map(this::mapToUserResponseDTO)
                .collect(java.util.stream.Collectors.toList());
        
        return ApiResponseDTO.success("Doctors retrieved successfully", response);
    }

    public ApiResponseDTO<UserResponseDTO> getDoctorById(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        
        if (!doctor.getIsVerified()) {
            return ApiResponseDTO.error("Doctor is not verified");
        }
        
        UserResponseDTO response = mapToUserResponseDTO(doctor);
        return ApiResponseDTO.success("Doctor retrieved successfully", response);
    }

    private UserResponseDTO mapToUserResponseDTO(Doctor doctor) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(doctor.getId());
        dto.setEmail(doctor.getEmail());
        dto.setFirstName(doctor.getFirstName());
        dto.setLastName(doctor.getLastName());
        dto.setPhoneNumber(doctor.getPhoneNumber());
        dto.setUserType("DOCTOR");
        dto.setIsVerified(doctor.getIsVerified());
        dto.setCreatedAt(doctor.getCreatedAt());
        dto.setSpecialization(doctor.getSpecialization());
        dto.setLicenseNumber(doctor.getLicenseNumber());
        dto.setYearsOfExperience(doctor.getYearsOfExperience());
        return dto;
    }
}


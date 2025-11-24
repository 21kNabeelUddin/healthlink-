package HealthLink.HelathLink.service;

import HealthLink.HelathLink.dto.*;
import HealthLink.HelathLink.entity.Admin;
import HealthLink.HelathLink.repository.AdminRepository;
import HealthLink.HelathLink.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final OtpService otpService;

    public ApiResponseDTO<String> signup(SignupRequestDTO signupRequest) {
        // Check if email already exists
        if (adminRepository.existsByEmail(signupRequest.getEmail())) {
            return ApiResponseDTO.error("Email already registered");
        }

        // Check if phone number already exists
        if (adminRepository.existsByPhoneNumber(signupRequest.getPhoneNumber())) {
            return ApiResponseDTO.error("Phone number already registered");
        }

        // Create admin entity
        Admin admin = new Admin();
        admin.setEmail(signupRequest.getEmail());
        admin.setPassword(PasswordUtil.hashPassword(signupRequest.getPassword()));
        admin.setFirstName(signupRequest.getFirstName());
        admin.setLastName(signupRequest.getLastName());
        admin.setPhoneNumber(signupRequest.getPhoneNumber());
        admin.setRole("ADMIN");
        admin.setIsVerified(false);

        adminRepository.save(admin);

        // Generate and send OTP
        String otp = otpService.generateOtp();
        otpService.saveOtp(signupRequest.getEmail(), otp, "ADMIN");
        
        try {
            otpService.sendOtpEmail(signupRequest.getEmail(), otp, "Admin");
            return ApiResponseDTO.success("Admin registered successfully. Please verify your email with OTP.", null);
        } catch (Exception e) {
            // If email sending fails, still return success but log the OTP for development
            System.err.println("Failed to send OTP email: " + e.getMessage());
            System.out.println("========================================");
            System.out.println("DEVELOPMENT MODE - OTP for " + signupRequest.getEmail() + ": " + otp);
            System.out.println("========================================");
            return ApiResponseDTO.success("Admin registered successfully. OTP email could not be sent. Please contact support.", null);
        }
    }

    @Transactional
    public ApiResponseDTO<UserResponseDTO> verifyOtp(OtpVerificationRequestDTO otpRequest) {
        if (!otpRequest.getUserType().equals("ADMIN")) {
            return ApiResponseDTO.error("Invalid user type for admin verification");
        }

        boolean isValid = otpService.verifyOtp(otpRequest.getEmail(), otpRequest.getOtp(), "ADMIN");
        if (!isValid) {
            return ApiResponseDTO.error("Invalid or expired OTP");
        }

        Admin admin = adminRepository.findByEmail(otpRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        admin.setIsVerified(true);
        adminRepository.save(admin);

        UserResponseDTO userResponse = mapToUserResponseDTO(admin);
        return ApiResponseDTO.success("Email verified successfully", userResponse);
    }

    public ApiResponseDTO<LoginResponseDTO> login(LoginRequestDTO loginRequest) {
        Admin admin = adminRepository.findByEmail(loginRequest.getEmail())
                .orElse(null);

        if (admin == null) {
            return ApiResponseDTO.error("Invalid email or password");
        }

        if (!PasswordUtil.verifyPassword(loginRequest.getPassword(), admin.getPassword())) {
            return ApiResponseDTO.error("Invalid email or password");
        }

        if (!admin.getIsVerified()) {
            return ApiResponseDTO.error("Please verify your email first");
        }

        UserResponseDTO userResponse = mapToUserResponseDTO(admin);
        LoginResponseDTO loginResponse = new LoginResponseDTO();
        loginResponse.setMessage("Login successful");
        loginResponse.setUser(userResponse);
        loginResponse.setToken(""); // For future JWT implementation

        return ApiResponseDTO.success("Login successful", loginResponse);
    }

    private UserResponseDTO mapToUserResponseDTO(Admin admin) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(admin.getId());
        dto.setEmail(admin.getEmail());
        dto.setFirstName(admin.getFirstName());
        dto.setLastName(admin.getLastName());
        dto.setPhoneNumber(admin.getPhoneNumber());
        dto.setUserType("ADMIN");
        dto.setIsVerified(admin.getIsVerified());
        dto.setCreatedAt(admin.getCreatedAt());
        dto.setRole(admin.getRole());
        return dto;
    }
}


package HealthLink.HelathLink.controller;

import HealthLink.HelathLink.dto.*;
import HealthLink.HelathLink.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDTO<String>> signup(@Valid @RequestBody SignupRequestDTO signupRequest) {
        ApiResponseDTO<String> response = doctorService.signup(signupRequest);
        HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> verifyOtp(@Valid @RequestBody OtpVerificationRequestDTO otpRequest) {
        ApiResponseDTO<UserResponseDTO> response = doctorService.verifyOtp(otpRequest);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        ApiResponseDTO<LoginResponseDTO> response = doctorService.login(loginRequest);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(response);
    }
}


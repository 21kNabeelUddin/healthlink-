package HealthLink.HelathLink.controller;

import HealthLink.HelathLink.dto.*;
import HealthLink.HelathLink.service.DoctorService;
import HealthLink.HelathLink.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final DoctorService doctorService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDTO<String>> signup(@Valid @RequestBody SignupRequestDTO signupRequest) {
        ApiResponseDTO<String> response = patientService.signup(signupRequest);
        HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> verifyOtp(@Valid @RequestBody OtpVerificationRequestDTO otpRequest) {
        ApiResponseDTO<UserResponseDTO> response = patientService.verifyOtp(otpRequest);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        ApiResponseDTO<LoginResponseDTO> response = patientService.login(loginRequest);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/doctors")
    public ResponseEntity<ApiResponseDTO<List<UserResponseDTO>>> getAllDoctors(
            @RequestParam(required = false) String specialization) {
        ApiResponseDTO<List<UserResponseDTO>> response = doctorService.getAllVerifiedDoctors(specialization);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/doctors/{doctorId}")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> getDoctorById(@PathVariable Long doctorId) {
        ApiResponseDTO<UserResponseDTO> response = doctorService.getDoctorById(doctorId);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }
}


package HealthLink.HelathLink.controller;

import HealthLink.HelathLink.dto.*;
import HealthLink.HelathLink.service.AdminManagementService;
import HealthLink.HelathLink.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final AdminManagementService adminManagementService;

    // ==================== AUTHENTICATION ====================

    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDTO<String>> signup(@Valid @RequestBody SignupRequestDTO signupRequest) {
        ApiResponseDTO<String> response = adminService.signup(signupRequest);
        HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> verifyOtp(@Valid @RequestBody OtpVerificationRequestDTO otpRequest) {
        ApiResponseDTO<UserResponseDTO> response = adminService.verifyOtp(otpRequest);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        ApiResponseDTO<LoginResponseDTO> response = adminService.login(loginRequest);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(response);
    }

    // ==================== DASHBOARD ====================

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponseDTO<AdminDashboardDTO>> getDashboardStats() {
        ApiResponseDTO<AdminDashboardDTO> response = adminManagementService.getDashboardStats();
        return ResponseEntity.ok(response);
    }

    // ==================== PATIENT MANAGEMENT ====================

    @GetMapping("/patients")
    public ResponseEntity<ApiResponseDTO<List<UserResponseDTO>>> getAllPatients() {
        ApiResponseDTO<List<UserResponseDTO>> response = adminManagementService.getAllPatients();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/patients/{patientId}")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> getPatientById(@PathVariable Long patientId) {
        ApiResponseDTO<UserResponseDTO> response = adminManagementService.getPatientById(patientId);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    @DeleteMapping("/patients/{patientId}")
    public ResponseEntity<ApiResponseDTO<String>> deletePatient(@PathVariable Long patientId) {
        ApiResponseDTO<String> response = adminManagementService.deletePatient(patientId);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    // ==================== DOCTOR MANAGEMENT ====================

    @GetMapping("/doctors")
    public ResponseEntity<ApiResponseDTO<List<UserResponseDTO>>> getAllDoctors() {
        ApiResponseDTO<List<UserResponseDTO>> response = adminManagementService.getAllDoctors();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/doctors/{doctorId}")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> getDoctorById(@PathVariable Long doctorId) {
        ApiResponseDTO<UserResponseDTO> response = adminManagementService.getDoctorById(doctorId);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    @DeleteMapping("/doctors/{doctorId}")
    public ResponseEntity<ApiResponseDTO<String>> deleteDoctor(@PathVariable Long doctorId) {
        ApiResponseDTO<String> response = adminManagementService.deleteDoctor(doctorId);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    // ==================== ADMIN MANAGEMENT ====================

    @GetMapping("/admins")
    public ResponseEntity<ApiResponseDTO<List<UserResponseDTO>>> getAllAdmins() {
        ApiResponseDTO<List<UserResponseDTO>> response = adminManagementService.getAllAdmins();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admins/{adminId}")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> getAdminById(@PathVariable Long adminId) {
        ApiResponseDTO<UserResponseDTO> response = adminManagementService.getAdminById(adminId);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    @DeleteMapping("/admins/{adminId}")
    public ResponseEntity<ApiResponseDTO<String>> deleteAdmin(@PathVariable Long adminId) {
        ApiResponseDTO<String> response = adminManagementService.deleteAdmin(adminId);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    // ==================== APPOINTMENT MANAGEMENT ====================

    @GetMapping("/appointments")
    public ResponseEntity<ApiResponseDTO<List<AppointmentResponseDTO>>> getAllAppointments(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String appointmentType) {
        ApiResponseDTO<List<AppointmentResponseDTO>> response = adminManagementService.getAllAppointments(status, appointmentType);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/appointments/{appointmentId}")
    public ResponseEntity<ApiResponseDTO<AppointmentResponseDTO>> getAppointmentById(@PathVariable Long appointmentId) {
        ApiResponseDTO<AppointmentResponseDTO> response = adminManagementService.getAppointmentById(appointmentId);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    @DeleteMapping("/appointments/{appointmentId}")
    public ResponseEntity<ApiResponseDTO<String>> deleteAppointment(@PathVariable Long appointmentId) {
        ApiResponseDTO<String> response = adminManagementService.deleteAppointment(appointmentId);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    @PatchMapping("/appointments/{appointmentId}/status")
    public ResponseEntity<ApiResponseDTO<AppointmentResponseDTO>> updateAppointmentStatus(
            @PathVariable Long appointmentId,
            @RequestParam String status) {
        ApiResponseDTO<AppointmentResponseDTO> response = adminManagementService.updateAppointmentStatus(appointmentId, status);
        HttpStatus httpStatus = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(httpStatus).body(response);
    }

    // ==================== CLINIC MANAGEMENT ====================

    @GetMapping("/clinics")
    public ResponseEntity<ApiResponseDTO<List<ClinicResponseDTO>>> getAllClinics(
            @RequestParam(required = false) Long doctorId) {
        ApiResponseDTO<List<ClinicResponseDTO>> response = adminManagementService.getAllClinics(doctorId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/clinics/{clinicId}")
    public ResponseEntity<ApiResponseDTO<ClinicResponseDTO>> getClinicById(@PathVariable Long clinicId) {
        ApiResponseDTO<ClinicResponseDTO> response = adminManagementService.getClinicById(clinicId);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    @DeleteMapping("/clinics/{clinicId}")
    public ResponseEntity<ApiResponseDTO<String>> deleteClinic(@PathVariable Long clinicId) {
        ApiResponseDTO<String> response = adminManagementService.deleteClinic(clinicId);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    // ==================== MEDICAL HISTORY MANAGEMENT ====================

    @GetMapping("/medical-histories")
    public ResponseEntity<ApiResponseDTO<List<MedicalHistoryResponseDTO>>> getAllMedicalHistories(
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) String status) {
        ApiResponseDTO<List<MedicalHistoryResponseDTO>> response = adminManagementService.getAllMedicalHistories(patientId, status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/medical-histories/{historyId}")
    public ResponseEntity<ApiResponseDTO<MedicalHistoryResponseDTO>> getMedicalHistoryById(@PathVariable Long historyId) {
        ApiResponseDTO<MedicalHistoryResponseDTO> response = adminManagementService.getMedicalHistoryById(historyId);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    @DeleteMapping("/medical-histories/{historyId}")
    public ResponseEntity<ApiResponseDTO<String>> deleteMedicalHistory(@PathVariable Long historyId) {
        ApiResponseDTO<String> response = adminManagementService.deleteMedicalHistory(historyId);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }
}


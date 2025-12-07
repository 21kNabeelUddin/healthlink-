package com.healthlink.domain.user.controller;

import com.healthlink.domain.user.service.DoctorService;
import com.healthlink.domain.user.dto.CreateEmergencyPatientRequest;
import com.healthlink.domain.user.dto.EmergencyPatientResponse;
import com.healthlink.domain.user.dto.CreateEmergencyPatientAndAppointmentRequest;
import com.healthlink.domain.user.dto.EmergencyPatientAndAppointmentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctors", description = "Doctor management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping("/{doctorId}/dashboard")
    @PreAuthorize("hasRole('DOCTOR') and principal.id == #doctorId")
    @Operation(summary = "Get doctor dashboard analytics")
    @ApiResponse(responseCode = "200", description = "Dashboard data retrieved")
    public ResponseEntity<com.healthlink.domain.user.dto.DoctorDashboardDTO> getDashboard(@PathVariable UUID doctorId) {
        return ResponseEntity.ok(doctorService.getDashboard(doctorId));
    }

    @GetMapping("/{doctorId}/refund-policy")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    @Operation(summary = "Get doctor refund policy")
    @ApiResponse(responseCode = "200", description = "Refund policy retrieved")
    public ResponseEntity<Map<String, Object>> getRefundPolicy(@PathVariable UUID doctorId) {
        com.healthlink.domain.user.entity.Doctor doctor = (com.healthlink.domain.user.entity.Doctor) doctorService
                .getDoctorById(doctorId);
        return ResponseEntity.ok(Map.of(
                "doctorId", doctorId,
                "cutoffMinutes", doctor.getRefundCutoffMinutes(),
                "deductionPercent", doctor.getRefundDeductionPercent(),
                "allowDoctorCancellationFullRefund", doctor.getAllowFullRefundOnDoctorCancellation()));
    }

    @PostMapping("/{doctorId}/emergency/patient")
    @PreAuthorize("hasRole('DOCTOR') and principal.id == #doctorId")
    @Operation(summary = "Create emergency patient account", 
              description = "Creates a patient account on the spot with auto-generated email and password. Patient is auto-approved and email-verified.")
    @ApiResponse(responseCode = "200", description = "Emergency patient created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "404", description = "Doctor not found")
    public ResponseEntity<EmergencyPatientResponse> createEmergencyPatient(
            @PathVariable UUID doctorId,
            @Valid @RequestBody CreateEmergencyPatientRequest request) {
        return ResponseEntity.ok(doctorService.createEmergencyPatient(doctorId, request));
    }

    @PostMapping("/{doctorId}/emergency/patient-and-appointment")
    @PreAuthorize("hasRole('DOCTOR') and principal.id == #doctorId")
    @Operation(summary = "Create emergency patient and book appointment", 
              description = "Creates a patient account and books an emergency appointment in one call. Convenient for emergency cases.")
    @ApiResponse(responseCode = "200", description = "Emergency patient and appointment created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "404", description = "Doctor or facility not found")
    public ResponseEntity<EmergencyPatientAndAppointmentResponse> createEmergencyPatientAndAppointment(
            @PathVariable UUID doctorId,
            @Valid @RequestBody CreateEmergencyPatientAndAppointmentRequest request) {
        return ResponseEntity.ok(doctorService.createEmergencyPatientAndAppointment(doctorId, request));
    }
}

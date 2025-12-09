package com.healthlink.domain.analytics.controller;

import com.healthlink.domain.analytics.dto.AdminDashboardResponse;
import com.healthlink.domain.analytics.dto.DoctorAnalyticsResponse;
import com.healthlink.domain.analytics.dto.PatientAnalyticsResponse;
import com.healthlink.domain.analytics.dto.OrganizationAnalyticsResponse;
import com.healthlink.domain.analytics.service.AdminAnalyticsService;
import com.healthlink.domain.analytics.service.DoctorAnalyticsService;
import com.healthlink.domain.analytics.service.PatientAnalyticsService;
import com.healthlink.domain.analytics.service.OrganizationAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Analytics endpoints for all roles")
@SecurityRequirement(name = "bearerAuth")
public class AnalyticsController {

    private final AdminAnalyticsService adminAnalyticsService;
    private final DoctorAnalyticsService doctorAnalyticsService;
    private final PatientAnalyticsService patientAnalyticsService;
    private final OrganizationAnalyticsService organizationAnalyticsService;
    private final com.healthlink.domain.user.repository.UserRepository userRepository;

    @GetMapping("/doctor/me")
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(summary = "Get current doctor's analytics")
    public ResponseEntity<DoctorAnalyticsResponse> getMyDoctorAnalytics(@AuthenticationPrincipal UserDetails userDetails) {
        UUID doctorId = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
        return ResponseEntity.ok(doctorAnalyticsService.getDoctorAnalytics(doctorId));
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get doctor analytics by ID (Admin only)")
    public ResponseEntity<DoctorAnalyticsResponse> getDoctorAnalytics(@PathVariable UUID doctorId) {
        return ResponseEntity.ok(doctorAnalyticsService.getDoctorAnalytics(doctorId));
    }

    @GetMapping("/patient/me")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Get current patient's analytics")
    public ResponseEntity<PatientAnalyticsResponse> getMyPatientAnalytics(@AuthenticationPrincipal UserDetails userDetails) {
        UUID patientId = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
        return ResponseEntity.ok(patientAnalyticsService.getPatientAnalytics(patientId));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get patient analytics by ID (Admin only)")
    public ResponseEntity<PatientAnalyticsResponse> getPatientAnalytics(@PathVariable UUID patientId) {
        return ResponseEntity.ok(patientAnalyticsService.getPatientAnalytics(patientId));
    }

    @GetMapping("/organization/me")
    @PreAuthorize("hasRole('ORGANIZATION')")
    @Operation(summary = "Get current organization's analytics")
    public ResponseEntity<OrganizationAnalyticsResponse> getMyOrganizationAnalytics(@AuthenticationPrincipal UserDetails userDetails) {
        UUID orgId = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
        return ResponseEntity.ok(organizationAnalyticsService.getOrganizationAnalytics(orgId));
    }

    @GetMapping("/organization/{organizationId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get organization analytics by ID (Admin only)")
    public ResponseEntity<OrganizationAnalyticsResponse> getOrganizationAnalytics(@PathVariable UUID organizationId) {
        return ResponseEntity.ok(organizationAnalyticsService.getOrganizationAnalytics(organizationId));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get admin dashboard analytics")
    public ResponseEntity<AdminDashboardResponse> getAdminDashboard() {
        System.out.println("========================================");
        System.out.println("ADMIN DASHBOARD ENDPOINT CALLED");
        System.out.println("========================================");
        log.info("Admin dashboard endpoint called");
        try {
            System.out.println("Calling adminAnalyticsService.getAdminDashboard()...");
            AdminDashboardResponse response = adminAnalyticsService.getAdminDashboard();
            System.out.println("Admin dashboard data loaded successfully!");
            System.out.println("Response: " + response);
            log.info("Admin dashboard data loaded successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("========================================");
            System.err.println("ERROR in getAdminDashboard endpoint");
            System.err.println("Message: " + e.getMessage());
            System.err.println("========================================");
            log.error("Error in getAdminDashboard endpoint", e);
            e.printStackTrace();
            throw e; // Re-throw to let Spring handle it properly
        }
    }
}

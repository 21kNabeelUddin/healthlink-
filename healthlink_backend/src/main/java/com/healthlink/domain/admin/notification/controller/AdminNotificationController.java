package com.healthlink.domain.admin.notification.controller;

import com.healthlink.domain.admin.notification.dto.*;
import com.healthlink.domain.admin.notification.service.AdminNotificationService;
import com.healthlink.domain.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/notifications")
@RequiredArgsConstructor
@Tag(name = "Admin Notifications", description = "Admin custom notification management")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminNotificationController {

    private final AdminNotificationService adminNotificationService;
    private final UserRepository userRepository;

    @PostMapping("/send")
    @Operation(summary = "Send custom notification to users/doctors")
    public ResponseEntity<AdminNotificationResponse> sendNotification(
            @Valid @RequestBody SendCustomNotificationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // Extract admin ID from user details (username is email)
        UUID adminId = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Admin user not found"))
                .getId();
        
        AdminNotificationResponse response = adminNotificationService.sendNotification(request, adminId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    @Operation(summary = "Get notification history")
    public ResponseEntity<NotificationHistoryResponse> getNotificationHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        UUID adminId = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Admin user not found"))
                .getId();
        
        NotificationHistoryResponse response = adminNotificationService.getNotificationHistory(adminId, page, size);
        return ResponseEntity.ok(response);
    }
}


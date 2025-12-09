package com.healthlink.domain.admin.notification.dto;

import com.healthlink.domain.admin.notification.entity.AdminNotification;
import com.healthlink.domain.admin.notification.entity.AdminNotificationPriority;
import com.healthlink.domain.admin.notification.entity.AdminNotificationStatus;
import com.healthlink.domain.admin.notification.entity.AdminNotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminNotificationResponse {
    private UUID id;
    private UUID sentByAdminId;
    private String sentByAdminName;
    private String title;
    private String message;
    private AdminNotificationType notificationType;
    private AdminNotificationPriority priority;
    private String recipientType;
    private Integer totalRecipients;
    private Integer sentCount;
    private Integer deliveredCount;
    private Integer failedCount;
    private String channels;
    private OffsetDateTime scheduledAt;
    private OffsetDateTime sentAt;
    private AdminNotificationStatus status;
    private LocalDateTime createdAt;
    
    public static AdminNotificationResponse fromEntity(AdminNotification notification) {
        return AdminNotificationResponse.builder()
                .id(notification.getId())
                .sentByAdminId(notification.getSentByAdminId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .notificationType(notification.getNotificationType())
                .priority(notification.getPriority())
                .recipientType(notification.getRecipientType())
                .totalRecipients(notification.getTotalRecipients())
                .sentCount(notification.getSentCount())
                .deliveredCount(notification.getDeliveredCount())
                .failedCount(notification.getFailedCount())
                .channels(notification.getChannels())
                .scheduledAt(notification.getScheduledAt())
                .sentAt(notification.getSentAt())
                .status(notification.getStatus())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}


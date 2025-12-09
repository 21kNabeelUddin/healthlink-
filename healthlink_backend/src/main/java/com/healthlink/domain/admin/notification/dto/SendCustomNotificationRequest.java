package com.healthlink.domain.admin.notification.dto;

import com.healthlink.domain.admin.notification.entity.AdminNotificationPriority;
import com.healthlink.domain.admin.notification.entity.AdminNotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class SendCustomNotificationRequest {
    
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;
    
    @NotBlank(message = "Message is required")
    @Size(max = 2000, message = "Message must not exceed 2000 characters")
    private String message;
    
    @NotNull(message = "Notification type is required")
    private AdminNotificationType notificationType;
    
    @NotNull(message = "Priority is required")
    private AdminNotificationPriority priority;
    
    @NotNull(message = "Recipient type is required")
    private RecipientType recipientType;
    
    private List<UUID> recipientIds; // For INDIVIDUAL or SELECTED recipients
    
    @NotNull(message = "Channels are required")
    private List<NotificationChannel> channels;
    
    private OffsetDateTime scheduledAt; // Optional: for scheduled notifications
    
    public enum RecipientType {
        INDIVIDUAL_USER,
        INDIVIDUAL_DOCTOR,
        ALL_USERS,
        ALL_DOCTORS,
        SELECTED_USERS,
        SELECTED_DOCTORS
    }
    
    public enum NotificationChannel {
        IN_APP,
        EMAIL,
        SMS,
        PUSH
    }
}


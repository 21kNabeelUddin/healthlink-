package com.healthlink.domain.admin.notification.entity;

import com.healthlink.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entity to track admin-sent custom notifications.
 * Stores metadata about notifications sent by admins to users/doctors.
 */
@Entity
@Table(name = "admin_notifications")
@Getter
@Setter
public class AdminNotification extends BaseEntity {

    @Column(name = "sent_by_admin_id", nullable = false)
    private UUID sentByAdminId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "message", nullable = false, length = 2000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 50)
    private AdminNotificationType notificationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    private AdminNotificationPriority priority;

    @Column(name = "recipient_type", nullable = false, length = 50)
    private String recipientType; // INDIVIDUAL, ALL_USERS, ALL_DOCTORS, SELECTED_USERS, SELECTED_DOCTORS

    @Column(name = "recipient_ids", length = 4000)
    private String recipientIds; // JSON array of UUIDs for individual/selected recipients

    @Column(name = "channels", nullable = false, length = 100)
    private String channels; // Comma-separated: IN_APP,EMAIL,SMS,PUSH

    @Column(name = "scheduled_at")
    private OffsetDateTime scheduledAt;

    @Column(name = "sent_at")
    private OffsetDateTime sentAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AdminNotificationStatus status = AdminNotificationStatus.PENDING;

    @Column(name = "total_recipients")
    private Integer totalRecipients;

    @Column(name = "sent_count")
    private Integer sentCount = 0;

    @Column(name = "delivered_count")
    private Integer deliveredCount = 0;

    @Column(name = "failed_count")
    private Integer failedCount = 0;

    @Column(name = "metadata", length = 4000)
    private String metadata; // JSON for additional data
}


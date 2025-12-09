package com.healthlink.domain.admin.notification.repository;

import com.healthlink.domain.admin.notification.entity.AdminNotification;
import com.healthlink.domain.admin.notification.entity.AdminNotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AdminNotificationRepository extends JpaRepository<AdminNotification, UUID> {
    
    Page<AdminNotification> findBySentByAdminIdOrderByCreatedAtDesc(UUID adminId, Pageable pageable);
    
    List<AdminNotification> findByStatusAndScheduledAtBefore(AdminNotificationStatus status, OffsetDateTime now);
    
    @Query("SELECT COUNT(a) FROM AdminNotification a WHERE a.sentByAdminId = :adminId")
    Long countByAdminId(@Param("adminId") UUID adminId);
}


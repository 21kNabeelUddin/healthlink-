package com.healthlink.domain.admin.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationHistoryResponse {
    private List<AdminNotificationResponse> notifications;
    private Long totalCount;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
}


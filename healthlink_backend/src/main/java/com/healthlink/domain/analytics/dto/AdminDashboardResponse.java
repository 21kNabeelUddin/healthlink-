package com.healthlink.domain.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {
    private Integer totalPatients;
    private Integer totalDoctors;
    private Integer totalAdmins;
    private Integer totalAppointments;
    private Integer totalClinics;
    private Integer totalMedicalHistories;
    private Integer pendingAppointments;
    private Integer confirmedAppointments;
    private Integer completedAppointments;
}


package HealthLink.HelathLink.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDTO {
    private Long totalPatients;
    private Long totalDoctors;
    private Long totalAdmins;
    private Long totalAppointments;
    private Long totalClinics;
    private Long totalMedicalHistories;
    private Long pendingAppointments;
    private Long confirmedAppointments;
    private Long completedAppointments;
}


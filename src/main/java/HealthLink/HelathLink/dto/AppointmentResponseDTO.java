package HealthLink.HelathLink.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDTO {
    private Long id;
    private LocalDateTime appointmentDateTime;
    private String reason;
    private String notes;
    private String status;
    private String appointmentType;
    private Long patientId;
    private String patientName;
    private String patientEmail;
    private Long doctorId;
    private String doctorName;
    private String doctorSpecialization;
    private Long clinicId;
    private String clinicName;
    private String clinicAddress;
    private String zoomMeetingId;
    private String zoomMeetingUrl;
    private String zoomMeetingPassword;
    private String zoomJoinUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


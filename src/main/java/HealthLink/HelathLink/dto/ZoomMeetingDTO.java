package HealthLink.HelathLink.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZoomMeetingDTO {
    private String meetingId;
    private String meetingUrl;
    private String password;
    private String joinUrl;
    private String appointmentDateTime;
    private String patientName;
    private String doctorName;
}


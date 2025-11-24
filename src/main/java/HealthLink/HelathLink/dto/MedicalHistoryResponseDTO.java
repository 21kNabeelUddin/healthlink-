package HealthLink.HelathLink.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalHistoryResponseDTO {
    private Long id;
    private String condition;
    private LocalDate diagnosisDate;
    private String description;
    private String treatment;
    private String medications;
    private String doctorName;
    private String hospitalName;
    private String status;
    private Long patientId;
    private String patientName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


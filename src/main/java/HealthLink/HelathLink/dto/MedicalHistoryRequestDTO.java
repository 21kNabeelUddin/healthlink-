package HealthLink.HelathLink.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalHistoryRequestDTO {
    @NotBlank(message = "Condition is required")
    @Size(max = 255, message = "Condition must not exceed 255 characters")
    private String condition;

    @NotNull(message = "Diagnosis date is required")
    @PastOrPresent(message = "Diagnosis date cannot be in the future")
    private LocalDate diagnosisDate;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @Size(max = 1000, message = "Treatment must not exceed 1000 characters")
    private String treatment;

    @Size(max = 1000, message = "Medications must not exceed 1000 characters")
    private String medications;

    @Size(max = 500, message = "Doctor name must not exceed 500 characters")
    private String doctorName;

    @Size(max = 500, message = "Hospital name must not exceed 500 characters")
    private String hospitalName;

    @NotBlank(message = "Status is required")
    private String status; // ACTIVE, RESOLVED, CHRONIC, UNDER_TREATMENT
}


package com.healthlink.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateEmergencyPatientRequest {
    @NotBlank(message = "Patient name is required")
    private String patientName;
    
    private String phoneNumber; // Optional
}


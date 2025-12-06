package com.healthlink.domain.user.dto;

import com.healthlink.domain.appointment.dto.CreateAppointmentRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateEmergencyPatientAndAppointmentRequest {
    @NotBlank(message = "Patient name is required")
    private String patientName;
    
    private String phoneNumber; // Optional
    
    @NotNull(message = "Appointment details are required")
    @Valid
    private CreateAppointmentRequest appointmentRequest;
}


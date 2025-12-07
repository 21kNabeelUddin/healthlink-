package com.healthlink.domain.user.dto;

import com.healthlink.domain.appointment.dto.AppointmentResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmergencyPatientAndAppointmentResponse {
    private EmergencyPatientResponse patient;
    private AppointmentResponse appointment;
}


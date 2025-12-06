package com.healthlink.domain.user.service;

import com.healthlink.domain.user.dto.DoctorDashboardDTO;
import com.healthlink.domain.user.dto.CreateEmergencyPatientRequest;
import com.healthlink.domain.user.dto.EmergencyPatientResponse;
import com.healthlink.domain.user.dto.CreateEmergencyPatientAndAppointmentRequest;
import com.healthlink.domain.user.dto.EmergencyPatientAndAppointmentResponse;
import java.util.UUID;

public interface DoctorService {
    DoctorDashboardDTO getDashboard(UUID doctorId);

    com.healthlink.domain.user.entity.Doctor getDoctorById(UUID doctorId);

    java.util.List<com.healthlink.domain.user.entity.Doctor> searchDoctors(String query);
    
    EmergencyPatientResponse createEmergencyPatient(UUID doctorId, CreateEmergencyPatientRequest request);
    
    EmergencyPatientAndAppointmentResponse createEmergencyPatientAndAppointment(UUID doctorId, CreateEmergencyPatientAndAppointmentRequest request);
}

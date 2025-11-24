package HealthLink.HelathLink.service;

import HealthLink.HelathLink.dto.ApiResponseDTO;
import HealthLink.HelathLink.dto.AppointmentRequestDTO;
import HealthLink.HelathLink.dto.AppointmentResponseDTO;
import HealthLink.HelathLink.dto.ZoomMeetingDTO;
import HealthLink.HelathLink.entity.Appointment;
import HealthLink.HelathLink.entity.Clinic;
import HealthLink.HelathLink.entity.Doctor;
import HealthLink.HelathLink.entity.Patient;
import HealthLink.HelathLink.repository.AppointmentRepository;
import HealthLink.HelathLink.repository.ClinicRepository;
import HealthLink.HelathLink.repository.DoctorRepository;
import HealthLink.HelathLink.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final ClinicRepository clinicRepository;
    private final ZoomService zoomService;

    public ApiResponseDTO<AppointmentResponseDTO> bookAppointment(Long patientId, AppointmentRequestDTO request) {
        // Verify patient exists and is verified
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        
        if (!patient.getIsVerified()) {
            return ApiResponseDTO.error("Patient must be verified to book appointments");
        }

        // Verify doctor exists and is verified
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        
        if (!doctor.getIsVerified()) {
            return ApiResponseDTO.error("Doctor is not verified");
        }

        // Validate appointment type
        String appointmentType = request.getAppointmentType().toUpperCase();
        if (!appointmentType.equals("ONLINE") && !appointmentType.equals("ONSITE")) {
            return ApiResponseDTO.error("Invalid appointment type. Must be ONLINE or ONSITE");
        }

        // Check if appointment time is in the past
        if (request.getAppointmentDateTime().isBefore(LocalDateTime.now())) {
            return ApiResponseDTO.error("Appointment date and time must be in the future");
        }

        // Check for conflicting appointments (same doctor, same time, not cancelled/rejected)
        boolean hasConflict = appointmentRepository.existsByDoctorIdAndAppointmentDateTimeAndStatusNot(
                request.getDoctorId(), 
                request.getAppointmentDateTime(), 
                "CANCELLED");
        
        if (hasConflict) {
            return ApiResponseDTO.error("Doctor already has an appointment at this time");
        }

        // Get clinic - required for ONSITE appointments
        Clinic clinic = null;
        if (appointmentType.equals("ONSITE")) {
            if (request.getClinicId() != null) {
                clinic = clinicRepository.findById(request.getClinicId())
                        .orElse(null);
                if (clinic != null && !clinic.getDoctor().getId().equals(doctor.getId())) {
                    return ApiResponseDTO.error("Clinic does not belong to the selected doctor");
                }
            } else {
                // Get doctor's first active clinic for ONSITE appointments
                List<Clinic> doctorClinics = clinicRepository.findByDoctorIdAndIsActiveTrue(doctor.getId());
                if (doctorClinics.isEmpty()) {
                    return ApiResponseDTO.error("Doctor has no active clinic. Clinic is required for ONSITE appointments");
                }
                clinic = doctorClinics.get(0);
            }
        } else {
            // For ONLINE appointments, clinic is optional
            if (request.getClinicId() != null) {
                clinic = clinicRepository.findById(request.getClinicId())
                        .orElse(null);
                if (clinic != null && !clinic.getDoctor().getId().equals(doctor.getId())) {
                    return ApiResponseDTO.error("Clinic does not belong to the selected doctor");
                }
            }
        }

        // Create appointment
        Appointment appointment = new Appointment();
        appointment.setAppointmentDateTime(request.getAppointmentDateTime());
        appointment.setReason(request.getReason());
        appointment.setNotes(request.getNotes());
        appointment.setStatus("PENDING");
        appointment.setAppointmentType(appointmentType);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setClinic(clinic);

        // Create Zoom meeting for ONLINE appointments
        if (appointmentType.equals("ONLINE")) {
            try {
                ZoomService.ZoomMeetingDetails zoomDetails = zoomService.createMeeting(
                    patient.getFirstName() + " " + patient.getLastName(),
                    doctor.getFirstName() + " " + doctor.getLastName(),
                    request.getAppointmentDateTime()
                );
                appointment.setZoomMeetingId(zoomDetails.getMeetingId());
                appointment.setZoomMeetingUrl(zoomDetails.getMeetingUrl());
                appointment.setZoomMeetingPassword(zoomDetails.getPassword());
                appointment.setZoomJoinUrl(zoomDetails.getJoinUrl());
            } catch (Exception e) {
                System.err.println("Failed to create Zoom meeting: " + e.getMessage());
                // Continue without Zoom meeting - can be added later
            }
        }

        Appointment savedAppointment = appointmentRepository.save(appointment);
        AppointmentResponseDTO response = mapToAppointmentResponseDTO(savedAppointment);

        return ApiResponseDTO.success("Appointment booked successfully. Waiting for doctor confirmation.", response);
    }

    public ApiResponseDTO<List<AppointmentResponseDTO>> getPatientAppointments(Long patientId, String status) {
        List<Appointment> appointments;
        if (status != null && !status.trim().isEmpty()) {
            appointments = appointmentRepository.findByPatientIdAndStatus(patientId, status.toUpperCase());
        } else {
            appointments = appointmentRepository.findByPatientIdOrderByAppointmentDateTimeDesc(patientId);
        }

        List<AppointmentResponseDTO> response = appointments.stream()
                .map(this::mapToAppointmentResponseDTO)
                .collect(Collectors.toList());

        return ApiResponseDTO.success("Appointments retrieved successfully", response);
    }

    public ApiResponseDTO<List<AppointmentResponseDTO>> getDoctorAppointments(Long doctorId, String status) {
        List<Appointment> appointments;
        if (status != null && !status.trim().isEmpty()) {
            appointments = appointmentRepository.findByDoctorIdAndStatus(doctorId, status.toUpperCase());
        } else {
            appointments = appointmentRepository.findByDoctorIdOrderByAppointmentDateTimeDesc(doctorId);
        }

        List<AppointmentResponseDTO> response = appointments.stream()
                .map(this::mapToAppointmentResponseDTO)
                .collect(Collectors.toList());

        return ApiResponseDTO.success("Appointments retrieved successfully", response);
    }

    public ApiResponseDTO<AppointmentResponseDTO> getAppointmentById(Long appointmentId, Long patientId) {
        Appointment appointment = appointmentRepository.findByIdAndPatientId(appointmentId, patientId)
                .orElseThrow(() -> new RuntimeException("Appointment not found or you don't have access to it"));

        AppointmentResponseDTO response = mapToAppointmentResponseDTO(appointment);
        return ApiResponseDTO.success("Appointment retrieved successfully", response);
    }

    @Transactional
    public ApiResponseDTO<AppointmentResponseDTO> updateAppointment(Long appointmentId, Long patientId, AppointmentRequestDTO request) {
        Appointment appointment = appointmentRepository.findByIdAndPatientId(appointmentId, patientId)
                .orElseThrow(() -> new RuntimeException("Appointment not found or you don't have access to it"));

        // Only allow updates to PENDING appointments
        if (!appointment.getStatus().equals("PENDING")) {
            return ApiResponseDTO.error("Only pending appointments can be updated");
        }

        // Validate appointment type
        String appointmentType = request.getAppointmentType().toUpperCase();
        if (!appointmentType.equals("ONLINE") && !appointmentType.equals("ONSITE")) {
            return ApiResponseDTO.error("Invalid appointment type. Must be ONLINE or ONSITE");
        }

        // Check if appointment time is in the past
        if (request.getAppointmentDateTime().isBefore(LocalDateTime.now())) {
            return ApiResponseDTO.error("Appointment date and time must be in the future");
        }

        // Check for conflicting appointments (excluding current appointment)
        boolean hasConflict = appointmentRepository.existsByDoctorIdAndAppointmentDateTimeAndStatusNot(
                appointment.getDoctor().getId(), 
                request.getAppointmentDateTime(), 
                "CANCELLED");
        
        if (hasConflict && !appointment.getAppointmentDateTime().equals(request.getAppointmentDateTime())) {
            return ApiResponseDTO.error("Doctor already has an appointment at this time");
        }

        // Update appointment
        appointment.setAppointmentDateTime(request.getAppointmentDateTime());
        appointment.setReason(request.getReason());
        appointment.setNotes(request.getNotes());
        appointment.setAppointmentType(appointmentType);

        // Update clinic based on appointment type
        if (appointmentType.equals("ONSITE")) {
            if (request.getClinicId() != null) {
                Clinic clinic = clinicRepository.findById(request.getClinicId())
                        .orElse(null);
                if (clinic != null && clinic.getDoctor().getId().equals(appointment.getDoctor().getId())) {
                    appointment.setClinic(clinic);
                } else {
                    return ApiResponseDTO.error("Clinic does not belong to the selected doctor");
                }
            } else {
                // Get doctor's first active clinic for ONSITE appointments
                List<Clinic> doctorClinics = clinicRepository.findByDoctorIdAndIsActiveTrue(appointment.getDoctor().getId());
                if (doctorClinics.isEmpty()) {
                    return ApiResponseDTO.error("Doctor has no active clinic. Clinic is required for ONSITE appointments");
                }
                appointment.setClinic(doctorClinics.get(0));
            }
        } else {
            // For ONLINE appointments, clinic is optional
            if (request.getClinicId() != null) {
                Clinic clinic = clinicRepository.findById(request.getClinicId())
                        .orElse(null);
                if (clinic != null && clinic.getDoctor().getId().equals(appointment.getDoctor().getId())) {
                    appointment.setClinic(clinic);
                }
            } else {
                appointment.setClinic(null);
            }
        }

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        AppointmentResponseDTO response = mapToAppointmentResponseDTO(updatedAppointment);

        return ApiResponseDTO.success("Appointment updated successfully", response);
    }

    @Transactional
    public ApiResponseDTO<String> cancelAppointment(Long appointmentId, Long patientId) {
        Appointment appointment = appointmentRepository.findByIdAndPatientId(appointmentId, patientId)
                .orElseThrow(() -> new RuntimeException("Appointment not found or you don't have access to it"));

        if (appointment.getStatus().equals("CANCELLED")) {
            return ApiResponseDTO.error("Appointment is already cancelled");
        }

        if (appointment.getStatus().equals("COMPLETED")) {
            return ApiResponseDTO.error("Cannot cancel a completed appointment");
        }

        appointment.setStatus("CANCELLED");
        appointmentRepository.save(appointment);

        return ApiResponseDTO.success("Appointment cancelled successfully", null);
    }

    @Transactional
    public ApiResponseDTO<AppointmentResponseDTO> confirmAppointment(Long appointmentId, Long doctorId) {
        Appointment appointment = appointmentRepository.findByIdAndDoctorId(appointmentId, doctorId)
                .orElseThrow(() -> new RuntimeException("Appointment not found or you don't have access to it"));

        if (!appointment.getStatus().equals("PENDING")) {
            return ApiResponseDTO.error("Only pending appointments can be confirmed");
        }

        appointment.setStatus("CONFIRMED");
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        AppointmentResponseDTO response = mapToAppointmentResponseDTO(updatedAppointment);

        return ApiResponseDTO.success("Appointment confirmed successfully", response);
    }

    @Transactional
    public ApiResponseDTO<AppointmentResponseDTO> rejectAppointment(Long appointmentId, Long doctorId) {
        Appointment appointment = appointmentRepository.findByIdAndDoctorId(appointmentId, doctorId)
                .orElseThrow(() -> new RuntimeException("Appointment not found or you don't have access to it"));

        if (!appointment.getStatus().equals("PENDING")) {
            return ApiResponseDTO.error("Only pending appointments can be rejected");
        }

        appointment.setStatus("REJECTED");
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        AppointmentResponseDTO response = mapToAppointmentResponseDTO(updatedAppointment);

        return ApiResponseDTO.success("Appointment rejected successfully", response);
    }

    @Transactional
    public ApiResponseDTO<AppointmentResponseDTO> completeAppointment(Long appointmentId, Long doctorId) {
        Appointment appointment = appointmentRepository.findByIdAndDoctorId(appointmentId, doctorId)
                .orElseThrow(() -> new RuntimeException("Appointment not found or you don't have access to it"));

        if (appointment.getStatus().equals("COMPLETED")) {
            return ApiResponseDTO.error("Appointment is already completed");
        }

        if (appointment.getStatus().equals("CANCELLED") || appointment.getStatus().equals("REJECTED")) {
            return ApiResponseDTO.error("Cannot complete a cancelled or rejected appointment");
        }

        appointment.setStatus("COMPLETED");
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        AppointmentResponseDTO response = mapToAppointmentResponseDTO(updatedAppointment);

        return ApiResponseDTO.success("Appointment marked as completed", response);
    }

    public ApiResponseDTO<ZoomMeetingDTO> getZoomMeetingDetails(Long appointmentId, Long patientId) {
        Appointment appointment = appointmentRepository.findByIdAndPatientId(appointmentId, patientId)
                .orElseThrow(() -> new RuntimeException("Appointment not found or you don't have access to it"));

        if (!appointment.getAppointmentType().equals("ONLINE")) {
            return ApiResponseDTO.error("Zoom meeting is only available for ONLINE appointments");
        }

        if (appointment.getZoomMeetingId() == null || appointment.getZoomMeetingId().isEmpty()) {
            return ApiResponseDTO.error("Zoom meeting not created for this appointment");
        }

        ZoomMeetingDTO zoomDTO = new ZoomMeetingDTO();
        zoomDTO.setMeetingId(appointment.getZoomMeetingId());
        zoomDTO.setMeetingUrl(appointment.getZoomMeetingUrl());
        zoomDTO.setPassword(appointment.getZoomMeetingPassword());
        zoomDTO.setJoinUrl(appointment.getZoomJoinUrl());
        zoomDTO.setAppointmentDateTime(appointment.getAppointmentDateTime().toString());
        zoomDTO.setPatientName(appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName());
        zoomDTO.setDoctorName(appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName());

        return ApiResponseDTO.success("Zoom meeting details retrieved successfully", zoomDTO);
    }

    public ApiResponseDTO<ZoomMeetingDTO> getZoomMeetingDetailsForDoctor(Long appointmentId, Long doctorId) {
        Appointment appointment = appointmentRepository.findByIdAndDoctorId(appointmentId, doctorId)
                .orElseThrow(() -> new RuntimeException("Appointment not found or you don't have access to it"));

        if (!appointment.getAppointmentType().equals("ONLINE")) {
            return ApiResponseDTO.error("Zoom meeting is only available for ONLINE appointments");
        }

        if (appointment.getZoomMeetingId() == null || appointment.getZoomMeetingId().isEmpty()) {
            return ApiResponseDTO.error("Zoom meeting not created for this appointment");
        }

        ZoomMeetingDTO zoomDTO = new ZoomMeetingDTO();
        zoomDTO.setMeetingId(appointment.getZoomMeetingId());
        zoomDTO.setMeetingUrl(appointment.getZoomMeetingUrl());
        zoomDTO.setPassword(appointment.getZoomMeetingPassword());
        zoomDTO.setJoinUrl(appointment.getZoomJoinUrl());
        zoomDTO.setAppointmentDateTime(appointment.getAppointmentDateTime().toString());
        zoomDTO.setPatientName(appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName());
        zoomDTO.setDoctorName(appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName());

        return ApiResponseDTO.success("Zoom meeting details retrieved successfully", zoomDTO);
    }

    private AppointmentResponseDTO mapToAppointmentResponseDTO(Appointment appointment) {
        AppointmentResponseDTO dto = new AppointmentResponseDTO();
        dto.setId(appointment.getId());
        dto.setAppointmentDateTime(appointment.getAppointmentDateTime());
        dto.setReason(appointment.getReason());
        dto.setNotes(appointment.getNotes());
        dto.setStatus(appointment.getStatus());
        dto.setAppointmentType(appointment.getAppointmentType());
        dto.setPatientId(appointment.getPatient().getId());
        dto.setPatientName(appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName());
        dto.setPatientEmail(appointment.getPatient().getEmail());
        dto.setDoctorId(appointment.getDoctor().getId());
        dto.setDoctorName(appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName());
        dto.setDoctorSpecialization(appointment.getDoctor().getSpecialization());
        
        if (appointment.getClinic() != null) {
            dto.setClinicId(appointment.getClinic().getId());
            dto.setClinicName(appointment.getClinic().getName());
            dto.setClinicAddress(appointment.getClinic().getAddress() + ", " + 
                                appointment.getClinic().getCity() + ", " + 
                                appointment.getClinic().getState());
        }
        
        // Set Zoom meeting details
        dto.setZoomMeetingId(appointment.getZoomMeetingId());
        dto.setZoomMeetingUrl(appointment.getZoomMeetingUrl());
        dto.setZoomMeetingPassword(appointment.getZoomMeetingPassword());
        dto.setZoomJoinUrl(appointment.getZoomJoinUrl());
        
        dto.setCreatedAt(appointment.getCreatedAt());
        dto.setUpdatedAt(appointment.getUpdatedAt());
        return dto;
    }
}


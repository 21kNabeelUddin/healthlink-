package HealthLink.HelathLink.service;

import HealthLink.HelathLink.dto.*;
import HealthLink.HelathLink.entity.*;
import HealthLink.HelathLink.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminManagementService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AdminRepository adminRepository;
    private final AppointmentRepository appointmentRepository;
    private final ClinicRepository clinicRepository;
    private final MedicalHistoryRepository medicalHistoryRepository;

    // ==================== PATIENT MANAGEMENT ====================

    public ApiResponseDTO<List<UserResponseDTO>> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();
        List<UserResponseDTO> response = patients.stream()
                .map(this::mapPatientToDTO)
                .collect(Collectors.toList());
        return ApiResponseDTO.success("Patients retrieved successfully", response);
    }

    public ApiResponseDTO<UserResponseDTO> getPatientById(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        return ApiResponseDTO.success("Patient retrieved successfully", mapPatientToDTO(patient));
    }

    @Transactional
    public ApiResponseDTO<String> deletePatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        patientRepository.delete(patient);
        return ApiResponseDTO.success("Patient deleted successfully", null);
    }

    // ==================== DOCTOR MANAGEMENT ====================

    public ApiResponseDTO<List<UserResponseDTO>> getAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        List<UserResponseDTO> response = doctors.stream()
                .map(this::mapDoctorToDTO)
                .collect(Collectors.toList());
        return ApiResponseDTO.success("Doctors retrieved successfully", response);
    }

    public ApiResponseDTO<UserResponseDTO> getDoctorById(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        return ApiResponseDTO.success("Doctor retrieved successfully", mapDoctorToDTO(doctor));
    }

    @Transactional
    public ApiResponseDTO<String> deleteDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        doctorRepository.delete(doctor);
        return ApiResponseDTO.success("Doctor deleted successfully", null);
    }

    // ==================== ADMIN MANAGEMENT ====================

    public ApiResponseDTO<List<UserResponseDTO>> getAllAdmins() {
        List<Admin> admins = adminRepository.findAll();
        List<UserResponseDTO> response = admins.stream()
                .map(this::mapAdminToDTO)
                .collect(Collectors.toList());
        return ApiResponseDTO.success("Admins retrieved successfully", response);
    }

    public ApiResponseDTO<UserResponseDTO> getAdminById(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        return ApiResponseDTO.success("Admin retrieved successfully", mapAdminToDTO(admin));
    }

    @Transactional
    public ApiResponseDTO<String> deleteAdmin(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        adminRepository.delete(admin);
        return ApiResponseDTO.success("Admin deleted successfully", null);
    }

    // ==================== APPOINTMENT MANAGEMENT ====================

    public ApiResponseDTO<List<AppointmentResponseDTO>> getAllAppointments(String status, String appointmentType) {
        List<Appointment> appointments = appointmentRepository.findAll();
        
        // Filter by status if provided
        if (status != null && !status.trim().isEmpty()) {
            appointments = appointments.stream()
                    .filter(a -> a.getStatus().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }
        
        // Filter by appointment type if provided
        if (appointmentType != null && !appointmentType.trim().isEmpty()) {
            appointments = appointments.stream()
                    .filter(a -> a.getAppointmentType().equalsIgnoreCase(appointmentType))
                    .collect(Collectors.toList());
        }
        
        List<AppointmentResponseDTO> response = appointments.stream()
                .map(this::mapAppointmentToDTO)
                .collect(Collectors.toList());
        return ApiResponseDTO.success("Appointments retrieved successfully", response);
    }

    public ApiResponseDTO<AppointmentResponseDTO> getAppointmentById(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        return ApiResponseDTO.success("Appointment retrieved successfully", mapAppointmentToDTO(appointment));
    }

    @Transactional
    public ApiResponseDTO<String> deleteAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointmentRepository.delete(appointment);
        return ApiResponseDTO.success("Appointment deleted successfully", null);
    }

    @Transactional
    public ApiResponseDTO<AppointmentResponseDTO> updateAppointmentStatus(Long appointmentId, String status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        appointment.setStatus(status.toUpperCase());
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return ApiResponseDTO.success("Appointment status updated successfully", mapAppointmentToDTO(updatedAppointment));
    }

    // ==================== CLINIC MANAGEMENT ====================

    public ApiResponseDTO<List<ClinicResponseDTO>> getAllClinics(Long doctorId) {
        List<Clinic> clinics;
        if (doctorId != null) {
            clinics = clinicRepository.findByDoctorId(doctorId);
        } else {
            clinics = clinicRepository.findAll();
        }
        
        List<ClinicResponseDTO> response = clinics.stream()
                .map(this::mapClinicToDTO)
                .collect(Collectors.toList());
        return ApiResponseDTO.success("Clinics retrieved successfully", response);
    }

    public ApiResponseDTO<ClinicResponseDTO> getClinicById(Long clinicId) {
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new RuntimeException("Clinic not found"));
        return ApiResponseDTO.success("Clinic retrieved successfully", mapClinicToDTO(clinic));
    }

    @Transactional
    public ApiResponseDTO<String> deleteClinic(Long clinicId) {
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new RuntimeException("Clinic not found"));
        clinicRepository.delete(clinic);
        return ApiResponseDTO.success("Clinic deleted successfully", null);
    }

    // ==================== MEDICAL HISTORY MANAGEMENT ====================

    public ApiResponseDTO<List<MedicalHistoryResponseDTO>> getAllMedicalHistories(Long patientId, String status) {
        List<MedicalHistory> histories;
        if (patientId != null) {
            if (status != null && !status.trim().isEmpty()) {
                histories = medicalHistoryRepository.findByPatientIdAndStatus(patientId, status.toUpperCase());
            } else {
                histories = medicalHistoryRepository.findByPatientId(patientId);
            }
        } else {
            histories = medicalHistoryRepository.findAll();
            if (status != null && !status.trim().isEmpty()) {
                histories = histories.stream()
                        .filter(h -> h.getStatus().equalsIgnoreCase(status))
                        .collect(Collectors.toList());
            }
        }
        
        List<MedicalHistoryResponseDTO> response = histories.stream()
                .map(this::mapMedicalHistoryToDTO)
                .collect(Collectors.toList());
        return ApiResponseDTO.success("Medical histories retrieved successfully", response);
    }

    public ApiResponseDTO<MedicalHistoryResponseDTO> getMedicalHistoryById(Long historyId) {
        MedicalHistory history = medicalHistoryRepository.findById(historyId)
                .orElseThrow(() -> new RuntimeException("Medical history not found"));
        return ApiResponseDTO.success("Medical history retrieved successfully", mapMedicalHistoryToDTO(history));
    }

    @Transactional
    public ApiResponseDTO<String> deleteMedicalHistory(Long historyId) {
        MedicalHistory history = medicalHistoryRepository.findById(historyId)
                .orElseThrow(() -> new RuntimeException("Medical history not found"));
        medicalHistoryRepository.delete(history);
        return ApiResponseDTO.success("Medical history deleted successfully", null);
    }

    // ==================== STATISTICS/DASHBOARD ====================

    public ApiResponseDTO<AdminDashboardDTO> getDashboardStats() {
        AdminDashboardDTO dashboard = new AdminDashboardDTO();
        dashboard.setTotalPatients(patientRepository.count());
        dashboard.setTotalDoctors(doctorRepository.count());
        dashboard.setTotalAdmins(adminRepository.count());
        dashboard.setTotalAppointments(appointmentRepository.count());
        dashboard.setTotalClinics(clinicRepository.count());
        dashboard.setTotalMedicalHistories(medicalHistoryRepository.count());
        
        // Count by status
        dashboard.setPendingAppointments(
            appointmentRepository.findAll().stream()
                .filter(a -> a.getStatus().equals("PENDING"))
                .count()
        );
        dashboard.setConfirmedAppointments(
            appointmentRepository.findAll().stream()
                .filter(a -> a.getStatus().equals("CONFIRMED"))
                .count()
        );
        dashboard.setCompletedAppointments(
            appointmentRepository.findAll().stream()
                .filter(a -> a.getStatus().equals("COMPLETED"))
                .count()
        );
        
        return ApiResponseDTO.success("Dashboard statistics retrieved successfully", dashboard);
    }

    // ==================== MAPPING METHODS ====================

    private UserResponseDTO mapPatientToDTO(Patient patient) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(patient.getId());
        dto.setEmail(patient.getEmail());
        dto.setFirstName(patient.getFirstName());
        dto.setLastName(patient.getLastName());
        dto.setPhoneNumber(patient.getPhoneNumber());
        dto.setUserType("PATIENT");
        dto.setIsVerified(patient.getIsVerified());
        dto.setCreatedAt(patient.getCreatedAt());
        dto.setDateOfBirth(patient.getDateOfBirth());
        dto.setAddress(patient.getAddress());
        return dto;
    }

    private UserResponseDTO mapDoctorToDTO(Doctor doctor) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(doctor.getId());
        dto.setEmail(doctor.getEmail());
        dto.setFirstName(doctor.getFirstName());
        dto.setLastName(doctor.getLastName());
        dto.setPhoneNumber(doctor.getPhoneNumber());
        dto.setUserType("DOCTOR");
        dto.setIsVerified(doctor.getIsVerified());
        dto.setCreatedAt(doctor.getCreatedAt());
        dto.setSpecialization(doctor.getSpecialization());
        dto.setLicenseNumber(doctor.getLicenseNumber());
        dto.setYearsOfExperience(doctor.getYearsOfExperience());
        return dto;
    }

    private UserResponseDTO mapAdminToDTO(Admin admin) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(admin.getId());
        dto.setEmail(admin.getEmail());
        dto.setFirstName(admin.getFirstName());
        dto.setLastName(admin.getLastName());
        dto.setPhoneNumber(admin.getPhoneNumber());
        dto.setUserType("ADMIN");
        dto.setIsVerified(admin.getIsVerified());
        dto.setCreatedAt(admin.getCreatedAt());
        dto.setRole(admin.getRole());
        return dto;
    }

    private AppointmentResponseDTO mapAppointmentToDTO(Appointment appointment) {
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
        dto.setZoomMeetingId(appointment.getZoomMeetingId());
        dto.setZoomMeetingUrl(appointment.getZoomMeetingUrl());
        dto.setZoomMeetingPassword(appointment.getZoomMeetingPassword());
        dto.setZoomJoinUrl(appointment.getZoomJoinUrl());
        dto.setCreatedAt(appointment.getCreatedAt());
        dto.setUpdatedAt(appointment.getUpdatedAt());
        return dto;
    }

    private ClinicResponseDTO mapClinicToDTO(Clinic clinic) {
        ClinicResponseDTO dto = new ClinicResponseDTO();
        dto.setId(clinic.getId());
        dto.setName(clinic.getName());
        dto.setAddress(clinic.getAddress());
        dto.setCity(clinic.getCity());
        dto.setState(clinic.getState());
        dto.setZipCode(clinic.getZipCode());
        dto.setPhoneNumber(clinic.getPhoneNumber());
        dto.setEmail(clinic.getEmail());
        dto.setDescription(clinic.getDescription());
        dto.setOpeningTime(clinic.getOpeningTime());
        dto.setClosingTime(clinic.getClosingTime());
        dto.setIsActive(clinic.getIsActive());
        dto.setDoctorId(clinic.getDoctor().getId());
        dto.setDoctorName(clinic.getDoctor().getFirstName() + " " + clinic.getDoctor().getLastName());
        dto.setCreatedAt(clinic.getCreatedAt());
        dto.setUpdatedAt(clinic.getUpdatedAt());
        return dto;
    }

    private MedicalHistoryResponseDTO mapMedicalHistoryToDTO(MedicalHistory history) {
        MedicalHistoryResponseDTO dto = new MedicalHistoryResponseDTO();
        dto.setId(history.getId());
        dto.setCondition(history.getCondition());
        dto.setDiagnosisDate(history.getDiagnosisDate());
        dto.setDescription(history.getDescription());
        dto.setTreatment(history.getTreatment());
        dto.setMedications(history.getMedications());
        dto.setDoctorName(history.getDoctorName());
        dto.setHospitalName(history.getHospitalName());
        dto.setStatus(history.getStatus());
        dto.setPatientId(history.getPatient().getId());
        dto.setPatientName(history.getPatient().getFirstName() + " " + history.getPatient().getLastName());
        dto.setCreatedAt(history.getCreatedAt());
        dto.setUpdatedAt(history.getUpdatedAt());
        return dto;
    }
}


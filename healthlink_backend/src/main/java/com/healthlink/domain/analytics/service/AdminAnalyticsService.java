package com.healthlink.domain.analytics.service;

import com.healthlink.domain.analytics.dto.AdminDashboardResponse;
import com.healthlink.domain.appointment.entity.AppointmentStatus;
import com.healthlink.domain.appointment.repository.AppointmentRepository;
import com.healthlink.domain.organization.repository.FacilityRepository;
import com.healthlink.domain.record.repository.MedicalRecordRepository;
import com.healthlink.domain.user.enums.UserRole;
import com.healthlink.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminAnalyticsService {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final FacilityRepository facilityRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    public AdminDashboardResponse getAdminDashboard() {
        System.out.println(">>> AdminAnalyticsService.getAdminDashboard() called");
        try {
            log.info("Loading admin dashboard data...");
            
            // Count users by role
            System.out.println(">>> Counting users by role...");
            log.debug("Counting users by role...");
            Long totalPatients = userRepository.countByRole(UserRole.PATIENT);
            Long totalDoctors = userRepository.countByRole(UserRole.DOCTOR);
            Long totalAdmins = userRepository.countByRole(UserRole.ADMIN);
            System.out.println(">>> Users counted - Patients: " + totalPatients + ", Doctors: " + totalDoctors + ", Admins: " + totalAdmins);
            log.debug("Users counted - Patients: {}, Doctors: {}, Admins: {}", totalPatients, totalDoctors, totalAdmins);
            
            // Count appointments
            System.out.println(">>> Counting appointments...");
            log.debug("Counting appointments...");
            Long totalAppointments = appointmentRepository.count();
            System.out.println(">>> Total appointments: " + totalAppointments);
            log.debug("Total appointments: {}", totalAppointments);
            
            System.out.println(">>> Counting appointments by status...");
            Long pendingAppointments = appointmentRepository.countByStatus(AppointmentStatus.IN_PROGRESS.name());
            System.out.println(">>> Pending appointments: " + pendingAppointments);
            Long confirmedAppointments = appointmentRepository.countByStatus(AppointmentStatus.IN_PROGRESS.name()); // Using IN_PROGRESS as confirmed
            System.out.println(">>> Confirmed appointments: " + confirmedAppointments);
            Long completedAppointments = appointmentRepository.countByStatus(AppointmentStatus.COMPLETED.name());
            System.out.println(">>> Completed appointments: " + completedAppointments);
            log.debug("Appointments by status - Pending: {}, Confirmed: {}, Completed: {}", pendingAppointments, confirmedAppointments, completedAppointments);
            
            // Count clinics (facilities)
            System.out.println(">>> Counting clinics...");
            log.debug("Counting clinics...");
            Long totalClinics = facilityRepository.count();
            System.out.println(">>> Total clinics: " + totalClinics);
            log.debug("Total clinics: {}", totalClinics);
            
            // Count medical records
            System.out.println(">>> Counting medical records...");
            log.debug("Counting medical records...");
            Long totalMedicalHistories = medicalRecordRepository.count();
            System.out.println(">>> Total medical records: " + totalMedicalHistories);
            log.debug("Total medical records: {}", totalMedicalHistories);

            return AdminDashboardResponse.builder()
                    .totalPatients(totalPatients != null ? totalPatients.intValue() : 0)
                    .totalDoctors(totalDoctors != null ? totalDoctors.intValue() : 0)
                    .totalAdmins(totalAdmins != null ? totalAdmins.intValue() : 0)
                    .totalAppointments(totalAppointments != null ? totalAppointments.intValue() : 0)
                    .totalClinics(totalClinics != null ? totalClinics.intValue() : 0)
                    .totalMedicalHistories(totalMedicalHistories != null ? totalMedicalHistories.intValue() : 0)
                    .pendingAppointments(pendingAppointments != null ? pendingAppointments.intValue() : 0)
                    .confirmedAppointments(confirmedAppointments != null ? confirmedAppointments.intValue() : 0)
                    .completedAppointments(completedAppointments != null ? completedAppointments.intValue() : 0)
                    .build();
        } catch (Exception e) {
            // Log error and return empty dashboard to prevent frontend crash
            System.err.println("========================================");
            System.err.println("ERROR in AdminAnalyticsService.getAdminDashboard()");
            System.err.println("Exception type: " + e.getClass().getName());
            System.err.println("Message: " + e.getMessage());
            System.err.println("========================================");
            log.error("Error loading admin dashboard", e);
            e.printStackTrace();
            return AdminDashboardResponse.builder()
                    .totalPatients(0)
                    .totalDoctors(0)
                    .totalAdmins(0)
                    .totalAppointments(0)
                    .totalClinics(0)
                    .totalMedicalHistories(0)
                    .pendingAppointments(0)
                    .confirmedAppointments(0)
                    .completedAppointments(0)
                    .build();
        }
    }
}


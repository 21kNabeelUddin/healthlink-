package HealthLink.HelathLink.repository;

import HealthLink.HelathLink.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByPatientIdOrderByAppointmentDateTimeDesc(Long patientId);
    List<Appointment> findByDoctorIdOrderByAppointmentDateTimeDesc(Long doctorId);
    List<Appointment> findByPatientIdAndStatus(Long patientId, String status);
    List<Appointment> findByDoctorIdAndStatus(Long doctorId, String status);
    Optional<Appointment> findByIdAndPatientId(Long id, Long patientId);
    Optional<Appointment> findByIdAndDoctorId(Long id, Long doctorId);
    boolean existsByDoctorIdAndAppointmentDateTimeAndStatusNot(Long doctorId, LocalDateTime appointmentDateTime, String status);
    List<Appointment> findByDoctorIdAndAppointmentDateTimeBetween(Long doctorId, LocalDateTime start, LocalDateTime end);
}


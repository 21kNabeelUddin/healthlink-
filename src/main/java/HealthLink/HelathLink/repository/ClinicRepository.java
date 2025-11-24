package HealthLink.HelathLink.repository;

import HealthLink.HelathLink.entity.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, Long> {
    List<Clinic> findByDoctorId(Long doctorId);
    List<Clinic> findByDoctorIdAndIsActiveTrue(Long doctorId);
    Optional<Clinic> findByIdAndDoctorId(Long id, Long doctorId);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}


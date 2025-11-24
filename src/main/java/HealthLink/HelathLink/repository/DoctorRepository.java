package HealthLink.HelathLink.repository;

import HealthLink.HelathLink.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByEmail(String email);
    Optional<Doctor> findByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    List<Doctor> findByIsVerifiedTrue();
    List<Doctor> findByIsVerifiedTrueAndSpecializationIgnoreCase(String specialization);
}


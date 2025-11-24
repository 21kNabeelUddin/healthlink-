package HealthLink.HelathLink.repository;

import HealthLink.HelathLink.entity.MedicalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, Long> {
    List<MedicalHistory> findByPatientId(Long patientId);
    List<MedicalHistory> findByPatientIdOrderByDiagnosisDateDesc(Long patientId);
    Optional<MedicalHistory> findByIdAndPatientId(Long id, Long patientId);
    List<MedicalHistory> findByPatientIdAndStatus(Long patientId, String status);
}


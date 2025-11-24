package HealthLink.HelathLink.service;

import HealthLink.HelathLink.dto.ApiResponseDTO;
import HealthLink.HelathLink.dto.MedicalHistoryRequestDTO;
import HealthLink.HelathLink.dto.MedicalHistoryResponseDTO;
import HealthLink.HelathLink.entity.MedicalHistory;
import HealthLink.HelathLink.entity.Patient;
import HealthLink.HelathLink.repository.MedicalHistoryRepository;
import HealthLink.HelathLink.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicalHistoryService {

    private final MedicalHistoryRepository medicalHistoryRepository;
    private final PatientRepository patientRepository;

    public ApiResponseDTO<MedicalHistoryResponseDTO> createMedicalHistory(Long patientId, MedicalHistoryRequestDTO request) {
        // Verify patient exists
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        // Validate status
        if (!isValidStatus(request.getStatus())) {
            return ApiResponseDTO.error("Invalid status. Allowed values: ACTIVE, RESOLVED, CHRONIC, UNDER_TREATMENT");
        }

        // Create medical history entity
        MedicalHistory medicalHistory = new MedicalHistory();
        medicalHistory.setCondition(request.getCondition());
        medicalHistory.setDiagnosisDate(request.getDiagnosisDate());
        medicalHistory.setDescription(request.getDescription());
        medicalHistory.setTreatment(request.getTreatment());
        medicalHistory.setMedications(request.getMedications());
        medicalHistory.setDoctorName(request.getDoctorName());
        medicalHistory.setHospitalName(request.getHospitalName());
        medicalHistory.setStatus(request.getStatus().toUpperCase());
        medicalHistory.setPatient(patient);

        MedicalHistory savedHistory = medicalHistoryRepository.save(medicalHistory);
        MedicalHistoryResponseDTO response = mapToMedicalHistoryResponseDTO(savedHistory);

        return ApiResponseDTO.success("Medical history created successfully", response);
    }

    public ApiResponseDTO<List<MedicalHistoryResponseDTO>> getAllMedicalHistories(Long patientId, String status) {
        List<MedicalHistory> histories;
        if (status != null && !status.trim().isEmpty()) {
            histories = medicalHistoryRepository.findByPatientIdAndStatus(patientId, status.toUpperCase());
        } else {
            histories = medicalHistoryRepository.findByPatientIdOrderByDiagnosisDateDesc(patientId);
        }

        List<MedicalHistoryResponseDTO> response = histories.stream()
                .map(this::mapToMedicalHistoryResponseDTO)
                .collect(Collectors.toList());

        return ApiResponseDTO.success("Medical histories retrieved successfully", response);
    }

    public ApiResponseDTO<MedicalHistoryResponseDTO> getMedicalHistoryById(Long historyId, Long patientId) {
        MedicalHistory medicalHistory = medicalHistoryRepository.findByIdAndPatientId(historyId, patientId)
                .orElseThrow(() -> new RuntimeException("Medical history not found or you don't have access to it"));

        MedicalHistoryResponseDTO response = mapToMedicalHistoryResponseDTO(medicalHistory);
        return ApiResponseDTO.success("Medical history retrieved successfully", response);
    }

    @Transactional
    public ApiResponseDTO<MedicalHistoryResponseDTO> updateMedicalHistory(Long historyId, Long patientId, MedicalHistoryRequestDTO request) {
        MedicalHistory medicalHistory = medicalHistoryRepository.findByIdAndPatientId(historyId, patientId)
                .orElseThrow(() -> new RuntimeException("Medical history not found or you don't have access to it"));

        // Validate status
        if (!isValidStatus(request.getStatus())) {
            return ApiResponseDTO.error("Invalid status. Allowed values: ACTIVE, RESOLVED, CHRONIC, UNDER_TREATMENT");
        }

        // Update medical history fields
        medicalHistory.setCondition(request.getCondition());
        medicalHistory.setDiagnosisDate(request.getDiagnosisDate());
        medicalHistory.setDescription(request.getDescription());
        medicalHistory.setTreatment(request.getTreatment());
        medicalHistory.setMedications(request.getMedications());
        medicalHistory.setDoctorName(request.getDoctorName());
        medicalHistory.setHospitalName(request.getHospitalName());
        medicalHistory.setStatus(request.getStatus().toUpperCase());

        MedicalHistory updatedHistory = medicalHistoryRepository.save(medicalHistory);
        MedicalHistoryResponseDTO response = mapToMedicalHistoryResponseDTO(updatedHistory);

        return ApiResponseDTO.success("Medical history updated successfully", response);
    }

    @Transactional
    public ApiResponseDTO<String> deleteMedicalHistory(Long historyId, Long patientId) {
        MedicalHistory medicalHistory = medicalHistoryRepository.findByIdAndPatientId(historyId, patientId)
                .orElseThrow(() -> new RuntimeException("Medical history not found or you don't have access to it"));

        medicalHistoryRepository.delete(medicalHistory);
        return ApiResponseDTO.success("Medical history deleted successfully", null);
    }

    private boolean isValidStatus(String status) {
        if (status == null) return false;
        String upperStatus = status.toUpperCase();
        return upperStatus.equals("ACTIVE") || 
               upperStatus.equals("RESOLVED") || 
               upperStatus.equals("CHRONIC") || 
               upperStatus.equals("UNDER_TREATMENT");
    }

    private MedicalHistoryResponseDTO mapToMedicalHistoryResponseDTO(MedicalHistory medicalHistory) {
        MedicalHistoryResponseDTO dto = new MedicalHistoryResponseDTO();
        dto.setId(medicalHistory.getId());
        dto.setCondition(medicalHistory.getCondition());
        dto.setDiagnosisDate(medicalHistory.getDiagnosisDate());
        dto.setDescription(medicalHistory.getDescription());
        dto.setTreatment(medicalHistory.getTreatment());
        dto.setMedications(medicalHistory.getMedications());
        dto.setDoctorName(medicalHistory.getDoctorName());
        dto.setHospitalName(medicalHistory.getHospitalName());
        dto.setStatus(medicalHistory.getStatus());
        dto.setPatientId(medicalHistory.getPatient().getId());
        dto.setPatientName(medicalHistory.getPatient().getFirstName() + " " + medicalHistory.getPatient().getLastName());
        dto.setCreatedAt(medicalHistory.getCreatedAt());
        dto.setUpdatedAt(medicalHistory.getUpdatedAt());
        return dto;
    }
}


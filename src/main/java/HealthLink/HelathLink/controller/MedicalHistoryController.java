package HealthLink.HelathLink.controller;

import HealthLink.HelathLink.dto.ApiResponseDTO;
import HealthLink.HelathLink.dto.MedicalHistoryRequestDTO;
import HealthLink.HelathLink.dto.MedicalHistoryResponseDTO;
import HealthLink.HelathLink.service.MedicalHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient/{patientId}/medical-history")
@RequiredArgsConstructor
public class MedicalHistoryController {

    private final MedicalHistoryService medicalHistoryService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<MedicalHistoryResponseDTO>> createMedicalHistory(
            @PathVariable Long patientId,
            @Valid @RequestBody MedicalHistoryRequestDTO request) {
        ApiResponseDTO<MedicalHistoryResponseDTO> response = medicalHistoryService.createMedicalHistory(patientId, request);
        HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<MedicalHistoryResponseDTO>>> getAllMedicalHistories(
            @PathVariable Long patientId,
            @RequestParam(required = false) String status) {
        ApiResponseDTO<List<MedicalHistoryResponseDTO>> response = medicalHistoryService.getAllMedicalHistories(patientId, status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{historyId}")
    public ResponseEntity<ApiResponseDTO<MedicalHistoryResponseDTO>> getMedicalHistoryById(
            @PathVariable Long patientId,
            @PathVariable Long historyId) {
        ApiResponseDTO<MedicalHistoryResponseDTO> response = medicalHistoryService.getMedicalHistoryById(historyId, patientId);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    @PutMapping("/{historyId}")
    public ResponseEntity<ApiResponseDTO<MedicalHistoryResponseDTO>> updateMedicalHistory(
            @PathVariable Long patientId,
            @PathVariable Long historyId,
            @Valid @RequestBody MedicalHistoryRequestDTO request) {
        ApiResponseDTO<MedicalHistoryResponseDTO> response = medicalHistoryService.updateMedicalHistory(historyId, patientId, request);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @DeleteMapping("/{historyId}")
    public ResponseEntity<ApiResponseDTO<String>> deleteMedicalHistory(
            @PathVariable Long patientId,
            @PathVariable Long historyId) {
        ApiResponseDTO<String> response = medicalHistoryService.deleteMedicalHistory(historyId, patientId);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }
}


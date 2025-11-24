package HealthLink.HelathLink.controller;

import HealthLink.HelathLink.dto.ApiResponseDTO;
import HealthLink.HelathLink.dto.ClinicRequestDTO;
import HealthLink.HelathLink.dto.ClinicResponseDTO;
import HealthLink.HelathLink.service.ClinicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor/{doctorId}/clinic")
@RequiredArgsConstructor
public class ClinicController {

    private final ClinicService clinicService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<ClinicResponseDTO>> createClinic(
            @PathVariable Long doctorId,
            @Valid @RequestBody ClinicRequestDTO clinicRequest) {
        ApiResponseDTO<ClinicResponseDTO> response = clinicService.createClinic(doctorId, clinicRequest);
        HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<ClinicResponseDTO>>> getAllClinics(
            @PathVariable Long doctorId,
            @RequestParam(required = false) Boolean active) {
        ApiResponseDTO<List<ClinicResponseDTO>> response;
        if (active != null && active) {
            response = clinicService.getActiveClinicsByDoctor(doctorId);
        } else {
            response = clinicService.getAllClinicsByDoctor(doctorId);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{clinicId}")
    public ResponseEntity<ApiResponseDTO<ClinicResponseDTO>> getClinicById(
            @PathVariable Long doctorId,
            @PathVariable Long clinicId) {
        ApiResponseDTO<ClinicResponseDTO> response = clinicService.getClinicById(clinicId, doctorId);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    @PutMapping("/{clinicId}")
    public ResponseEntity<ApiResponseDTO<ClinicResponseDTO>> updateClinic(
            @PathVariable Long doctorId,
            @PathVariable Long clinicId,
            @Valid @RequestBody ClinicRequestDTO clinicRequest) {
        ApiResponseDTO<ClinicResponseDTO> response = clinicService.updateClinic(clinicId, doctorId, clinicRequest);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @DeleteMapping("/{clinicId}")
    public ResponseEntity<ApiResponseDTO<String>> deleteClinic(
            @PathVariable Long doctorId,
            @PathVariable Long clinicId) {
        ApiResponseDTO<String> response = clinicService.deleteClinic(clinicId, doctorId);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    @PatchMapping("/{clinicId}/toggle-status")
    public ResponseEntity<ApiResponseDTO<ClinicResponseDTO>> toggleClinicStatus(
            @PathVariable Long doctorId,
            @PathVariable Long clinicId) {
        ApiResponseDTO<ClinicResponseDTO> response = clinicService.toggleClinicStatus(clinicId, doctorId);
        return ResponseEntity.ok(response);
    }
}


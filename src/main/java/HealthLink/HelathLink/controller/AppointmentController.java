package HealthLink.HelathLink.controller;

import HealthLink.HelathLink.dto.ApiResponseDTO;
import HealthLink.HelathLink.dto.AppointmentRequestDTO;
import HealthLink.HelathLink.dto.AppointmentResponseDTO;
import HealthLink.HelathLink.dto.ZoomMeetingDTO;
import HealthLink.HelathLink.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient/{patientId}/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<AppointmentResponseDTO>> bookAppointment(
            @PathVariable Long patientId,
            @Valid @RequestBody AppointmentRequestDTO request) {
        ApiResponseDTO<AppointmentResponseDTO> response = appointmentService.bookAppointment(patientId, request);
        HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<AppointmentResponseDTO>>> getPatientAppointments(
            @PathVariable Long patientId,
            @RequestParam(required = false) String status) {
        ApiResponseDTO<List<AppointmentResponseDTO>> response = appointmentService.getPatientAppointments(patientId, status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{appointmentId}")
    public ResponseEntity<ApiResponseDTO<AppointmentResponseDTO>> getAppointmentById(
            @PathVariable Long patientId,
            @PathVariable Long appointmentId) {
        ApiResponseDTO<AppointmentResponseDTO> response = appointmentService.getAppointmentById(appointmentId, patientId);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    @PutMapping("/{appointmentId}")
    public ResponseEntity<ApiResponseDTO<AppointmentResponseDTO>> updateAppointment(
            @PathVariable Long patientId,
            @PathVariable Long appointmentId,
            @Valid @RequestBody AppointmentRequestDTO request) {
        ApiResponseDTO<AppointmentResponseDTO> response = appointmentService.updateAppointment(appointmentId, patientId, request);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/{appointmentId}/cancel")
    public ResponseEntity<ApiResponseDTO<String>> cancelAppointment(
            @PathVariable Long patientId,
            @PathVariable Long appointmentId) {
        ApiResponseDTO<String> response = appointmentService.cancelAppointment(appointmentId, patientId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{appointmentId}/zoom-meeting")
    public ResponseEntity<ApiResponseDTO<ZoomMeetingDTO>> getZoomMeetingDetails(
            @PathVariable Long patientId,
            @PathVariable Long appointmentId) {
        ApiResponseDTO<ZoomMeetingDTO> response = appointmentService.getZoomMeetingDetails(appointmentId, patientId);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }
}


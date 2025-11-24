package HealthLink.HelathLink.controller;

import HealthLink.HelathLink.dto.ApiResponseDTO;
import HealthLink.HelathLink.dto.AppointmentResponseDTO;
import HealthLink.HelathLink.dto.ZoomMeetingDTO;
import HealthLink.HelathLink.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor/{doctorId}/appointments")
@RequiredArgsConstructor
public class DoctorAppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<AppointmentResponseDTO>>> getDoctorAppointments(
            @PathVariable Long doctorId,
            @RequestParam(required = false) String status) {
        ApiResponseDTO<List<AppointmentResponseDTO>> response = appointmentService.getDoctorAppointments(doctorId, status);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{appointmentId}/confirm")
    public ResponseEntity<ApiResponseDTO<AppointmentResponseDTO>> confirmAppointment(
            @PathVariable Long doctorId,
            @PathVariable Long appointmentId) {
        ApiResponseDTO<AppointmentResponseDTO> response = appointmentService.confirmAppointment(appointmentId, doctorId);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/{appointmentId}/reject")
    public ResponseEntity<ApiResponseDTO<AppointmentResponseDTO>> rejectAppointment(
            @PathVariable Long doctorId,
            @PathVariable Long appointmentId) {
        ApiResponseDTO<AppointmentResponseDTO> response = appointmentService.rejectAppointment(appointmentId, doctorId);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/{appointmentId}/complete")
    public ResponseEntity<ApiResponseDTO<AppointmentResponseDTO>> completeAppointment(
            @PathVariable Long doctorId,
            @PathVariable Long appointmentId) {
        ApiResponseDTO<AppointmentResponseDTO> response = appointmentService.completeAppointment(appointmentId, doctorId);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/{appointmentId}/zoom-meeting")
    public ResponseEntity<ApiResponseDTO<ZoomMeetingDTO>> getZoomMeetingDetails(
            @PathVariable Long doctorId,
            @PathVariable Long appointmentId) {
        ApiResponseDTO<ZoomMeetingDTO> response = appointmentService.getZoomMeetingDetailsForDoctor(appointmentId, doctorId);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }
}


package HealthLink.HelathLink.service;

import HealthLink.HelathLink.dto.ApiResponseDTO;
import HealthLink.HelathLink.dto.ClinicRequestDTO;
import HealthLink.HelathLink.dto.ClinicResponseDTO;
import HealthLink.HelathLink.entity.Clinic;
import HealthLink.HelathLink.entity.Doctor;
import HealthLink.HelathLink.repository.ClinicRepository;
import HealthLink.HelathLink.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClinicService {

    private final ClinicRepository clinicRepository;
    private final DoctorRepository doctorRepository;

    public ApiResponseDTO<ClinicResponseDTO> createClinic(Long doctorId, ClinicRequestDTO clinicRequest) {
        // Verify doctor exists
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Check if email already exists
        if (clinicRepository.existsByEmail(clinicRequest.getEmail())) {
            return ApiResponseDTO.error("Clinic with this email already exists");
        }

        // Check if phone number already exists
        if (clinicRepository.existsByPhoneNumber(clinicRequest.getPhoneNumber())) {
            return ApiResponseDTO.error("Clinic with this phone number already exists");
        }

        // Validate opening and closing times
        if (clinicRequest.getClosingTime().isBefore(clinicRequest.getOpeningTime()) ||
            clinicRequest.getClosingTime().equals(clinicRequest.getOpeningTime())) {
            return ApiResponseDTO.error("Closing time must be after opening time");
        }

        // Create clinic entity
        Clinic clinic = new Clinic();
        clinic.setName(clinicRequest.getName());
        clinic.setAddress(clinicRequest.getAddress());
        clinic.setCity(clinicRequest.getCity());
        clinic.setState(clinicRequest.getState());
        clinic.setZipCode(clinicRequest.getZipCode());
        clinic.setPhoneNumber(clinicRequest.getPhoneNumber());
        clinic.setEmail(clinicRequest.getEmail());
        clinic.setDescription(clinicRequest.getDescription());
        clinic.setOpeningTime(clinicRequest.getOpeningTime());
        clinic.setClosingTime(clinicRequest.getClosingTime());
        clinic.setDoctor(doctor);
        clinic.setIsActive(true);

        Clinic savedClinic = clinicRepository.save(clinic);
        ClinicResponseDTO response = mapToClinicResponseDTO(savedClinic);

        return ApiResponseDTO.success("Clinic created successfully", response);
    }

    public ApiResponseDTO<List<ClinicResponseDTO>> getAllClinicsByDoctor(Long doctorId) {
        List<Clinic> clinics = clinicRepository.findByDoctorId(doctorId);
        List<ClinicResponseDTO> response = clinics.stream()
                .map(this::mapToClinicResponseDTO)
                .collect(Collectors.toList());
        return ApiResponseDTO.success("Clinics retrieved successfully", response);
    }

    public ApiResponseDTO<List<ClinicResponseDTO>> getActiveClinicsByDoctor(Long doctorId) {
        List<Clinic> clinics = clinicRepository.findByDoctorIdAndIsActiveTrue(doctorId);
        List<ClinicResponseDTO> response = clinics.stream()
                .map(this::mapToClinicResponseDTO)
                .collect(Collectors.toList());
        return ApiResponseDTO.success("Active clinics retrieved successfully", response);
    }

    public ApiResponseDTO<ClinicResponseDTO> getClinicById(Long clinicId, Long doctorId) {
        Clinic clinic = clinicRepository.findByIdAndDoctorId(clinicId, doctorId)
                .orElseThrow(() -> new RuntimeException("Clinic not found or you don't have access to it"));
        
        ClinicResponseDTO response = mapToClinicResponseDTO(clinic);
        return ApiResponseDTO.success("Clinic retrieved successfully", response);
    }

    @Transactional
    public ApiResponseDTO<ClinicResponseDTO> updateClinic(Long clinicId, Long doctorId, ClinicRequestDTO clinicRequest) {
        Clinic clinic = clinicRepository.findByIdAndDoctorId(clinicId, doctorId)
                .orElseThrow(() -> new RuntimeException("Clinic not found or you don't have access to it"));

        // Check if email already exists (excluding current clinic)
        if (clinicRepository.existsByEmail(clinicRequest.getEmail()) && 
            !clinic.getEmail().equals(clinicRequest.getEmail())) {
            return ApiResponseDTO.error("Clinic with this email already exists");
        }

        // Check if phone number already exists (excluding current clinic)
        if (clinicRepository.existsByPhoneNumber(clinicRequest.getPhoneNumber()) && 
            !clinic.getPhoneNumber().equals(clinicRequest.getPhoneNumber())) {
            return ApiResponseDTO.error("Clinic with this phone number already exists");
        }

        // Validate opening and closing times
        if (clinicRequest.getClosingTime().isBefore(clinicRequest.getOpeningTime()) ||
            clinicRequest.getClosingTime().equals(clinicRequest.getOpeningTime())) {
            return ApiResponseDTO.error("Closing time must be after opening time");
        }

        // Update clinic fields
        clinic.setName(clinicRequest.getName());
        clinic.setAddress(clinicRequest.getAddress());
        clinic.setCity(clinicRequest.getCity());
        clinic.setState(clinicRequest.getState());
        clinic.setZipCode(clinicRequest.getZipCode());
        clinic.setPhoneNumber(clinicRequest.getPhoneNumber());
        clinic.setEmail(clinicRequest.getEmail());
        clinic.setDescription(clinicRequest.getDescription());
        clinic.setOpeningTime(clinicRequest.getOpeningTime());
        clinic.setClosingTime(clinicRequest.getClosingTime());

        Clinic updatedClinic = clinicRepository.save(clinic);
        ClinicResponseDTO response = mapToClinicResponseDTO(updatedClinic);

        return ApiResponseDTO.success("Clinic updated successfully", response);
    }

    @Transactional
    public ApiResponseDTO<String> deleteClinic(Long clinicId, Long doctorId) {
        Clinic clinic = clinicRepository.findByIdAndDoctorId(clinicId, doctorId)
                .orElseThrow(() -> new RuntimeException("Clinic not found or you don't have access to it"));

        clinicRepository.delete(clinic);
        return ApiResponseDTO.success("Clinic deleted successfully", null);
    }

    @Transactional
    public ApiResponseDTO<ClinicResponseDTO> toggleClinicStatus(Long clinicId, Long doctorId) {
        Clinic clinic = clinicRepository.findByIdAndDoctorId(clinicId, doctorId)
                .orElseThrow(() -> new RuntimeException("Clinic not found or you don't have access to it"));

        clinic.setIsActive(!clinic.getIsActive());
        Clinic updatedClinic = clinicRepository.save(clinic);
        ClinicResponseDTO response = mapToClinicResponseDTO(updatedClinic);

        String message = clinic.getIsActive() ? "Clinic activated successfully" : "Clinic deactivated successfully";
        return ApiResponseDTO.success(message, response);
    }

    private ClinicResponseDTO mapToClinicResponseDTO(Clinic clinic) {
        ClinicResponseDTO dto = new ClinicResponseDTO();
        dto.setId(clinic.getId());
        dto.setName(clinic.getName());
        dto.setAddress(clinic.getAddress());
        dto.setCity(clinic.getCity());
        dto.setState(clinic.getState());
        dto.setZipCode(clinic.getZipCode());
        dto.setPhoneNumber(clinic.getPhoneNumber());
        dto.setEmail(clinic.getEmail());
        dto.setDescription(clinic.getDescription());
        dto.setOpeningTime(clinic.getOpeningTime());
        dto.setClosingTime(clinic.getClosingTime());
        dto.setIsActive(clinic.getIsActive());
        dto.setDoctorId(clinic.getDoctor().getId());
        dto.setDoctorName(clinic.getDoctor().getFirstName() + " " + clinic.getDoctor().getLastName());
        dto.setCreatedAt(clinic.getCreatedAt());
        dto.setUpdatedAt(clinic.getUpdatedAt());
        return dto;
    }
}


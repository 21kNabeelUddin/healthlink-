package HealthLink.HelathLink.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClinicResponseDTO {
    private Long id;
    private String name;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String phoneNumber;
    private String email;
    private String description;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private Boolean isActive;
    private Long doctorId;
    private String doctorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


package HealthLink.HelathLink.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String userType;
    private Boolean isVerified;
    private LocalDateTime createdAt;

    // Patient specific
    private LocalDateTime dateOfBirth;
    private String address;

    // Doctor specific
    private String specialization;
    private String licenseNumber;
    private Integer yearsOfExperience;

    // Admin specific
    private String role;
}


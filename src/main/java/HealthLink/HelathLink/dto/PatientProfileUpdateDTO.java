package HealthLink.HelathLink.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PatientProfileUpdateDTO {

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\d{11}$", message = "Phone number must be 11 digits")
    private String phoneNumber;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must be 255 characters or less")
    private String address;
}


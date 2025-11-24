package HealthLink.HelathLink.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_histories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "medical_condition", nullable = false)
    private String condition;

    @Column(nullable = false)
    private LocalDate diagnosisDate;

    @Column(length = 2000)
    private String description;

    @Column(length = 1000)
    private String treatment;

    @Column(length = 1000)
    private String medications;

    @Column(length = 500)
    private String doctorName;

    @Column(length = 500)
    private String hospitalName;

    @Column(nullable = false)
    private String status; // ACTIVE, RESOLVED, CHRONIC, UNDER_TREATMENT

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}


package com.learnvault.certificationbadgemanagement.entity;

import com.learnvault.certificationbadgemanagement.entity.enums.CertificationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "certifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer certificationId;

    private Integer courseId;

    private Integer learnerId;

    private LocalDate issuedDate;

    private LocalDate expiryDate;

    @Column(unique = true, length = 100)
    private String certificateNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CertificationStatus status = CertificationStatus.VALID;
}
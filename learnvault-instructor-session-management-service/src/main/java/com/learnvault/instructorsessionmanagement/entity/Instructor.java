package com.learnvault.instructorsessionmanagement.entity;

import com.learnvault.instructorsessionmanagement.entity.enums.RegistrationSource;
import com.learnvault.instructorsessionmanagement.entity.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "instructors")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Instructor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer instructorId;

    @Column(unique = true, nullable = false)
    private Integer userId;

    @Column(columnDefinition = "TEXT")
    private String specializations;

    @Column(length = 100)
    private String qualificationLevel;

    @Column(length = 100)
    private String experience;

    @Builder.Default
    private Double ratingAvg = 0.00;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RegistrationSource registrationSource = RegistrationSource.ADMIN_CREATED;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;
}

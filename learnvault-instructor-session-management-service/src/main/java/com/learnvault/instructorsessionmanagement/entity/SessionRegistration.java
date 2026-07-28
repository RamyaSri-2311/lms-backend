package com.learnvault.instructorsessionmanagement.entity;

import com.learnvault.instructorsessionmanagement.entity.enums.AttendanceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Entity
@Table(name = "session_registrations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer registrationId;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private TrainingSession session;

    private Integer learnerId;

    @CreationTimestamp
    private LocalDate registeredDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AttendanceStatus attendanceStatus = AttendanceStatus.REGISTERED;
}
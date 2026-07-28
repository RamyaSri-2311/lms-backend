package com.learnvault.assessmentevaluation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "attempt_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttemptRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer attemptId;

    @ManyToOne
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    private Integer learnerId;

    @Column(nullable = false)
    private Integer attemptNumber;

    private Integer score;

    private Boolean passed;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime attemptDate;

    private Integer timeTakenMinutes;
}
package com.learnvault.instructorsessionmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "session_feedback",
        uniqueConstraints = @UniqueConstraint(columnNames = {"session_id", "learner_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer feedbackId;

    @Column(name = "session_id", nullable = false)
    private Integer sessionId;

    @Column(name = "learner_id", nullable = false)
    private Integer learnerId;

    @Column(nullable = false)
    private Integer rating;              // 1-5 overall stars

    private Integer sessionQuality;      // 1-5
    private Integer instructorKnowledge; // 1-5
    private Integer contentRelevance;    // 1-5

    @Column(columnDefinition = "TEXT")
    private String comments;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime submittedDate;
}

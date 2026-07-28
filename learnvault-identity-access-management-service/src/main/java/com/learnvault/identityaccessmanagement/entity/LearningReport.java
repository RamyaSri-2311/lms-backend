package com.learnvault.identityaccessmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "learning_reports")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reportId;

    @Column(nullable = false, length = 50)
    private String scope;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String metrics;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime generatedDate;
}

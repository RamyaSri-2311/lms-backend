package com.learnvault.enrollmentlearningprogress.entity;

import com.learnvault.enrollmentlearningprogress.entity.enums.ModuleProgressStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "module_progress")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer progressId;

    @ManyToOne
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    private Integer moduleId;

    private LocalDate startedDate;

    private LocalDate completedDate;

    @Builder.Default
    private Integer timeSpentMinutes = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ModuleProgressStatus status = ModuleProgressStatus.NOTSTARTED;
}
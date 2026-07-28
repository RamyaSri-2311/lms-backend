package com.learnvault.coursecatalogcontentmanagement.entity;

import com.learnvault.coursecatalogcontentmanagement.entity.enums.LearningPathStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "learning_paths")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningPath {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pathId;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 100)
    private String targetRole;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String courseSequence;

    @Column(nullable = false)
    private Integer totalHours;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private LearningPathStatus status = LearningPathStatus.ACTIVE;
}
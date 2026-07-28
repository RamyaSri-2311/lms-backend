package com.learnvault.certificationbadgemanagement.entity;

import com.learnvault.certificationbadgemanagement.entity.enums.BadgeAwardStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Entity
@Table(name = "badge_awards")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeAward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer awardId;

    @ManyToOne
    @JoinColumn(name = "badge_id", nullable = false)
    private Badge badge;

    private Integer learnerId;

    @CreationTimestamp
    private LocalDate awardedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BadgeAwardStatus status = BadgeAwardStatus.ACTIVE;
}
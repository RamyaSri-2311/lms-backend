package com.learnvault.certificationbadgemanagement.service.impl;

import com.learnvault.certificationbadgemanagement.dto.request.CertificationRequest;
import com.learnvault.certificationbadgemanagement.dto.response.CertificationResponse;
import com.learnvault.certificationbadgemanagement.entity.Certification;
import com.learnvault.certificationbadgemanagement.entity.enums.CertificationStatus;
import com.learnvault.certificationbadgemanagement.exception.ResourceNotFoundException;
import com.learnvault.certificationbadgemanagement.repository.BadgeAwardRepository;
import com.learnvault.certificationbadgemanagement.repository.BadgeRepository;
import com.learnvault.certificationbadgemanagement.repository.CertificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CertificationServiceImplTest {

    @Mock
    private CertificationRepository certificationRepository;

    @Mock
    private BadgeRepository badgeRepository;

    @Mock
    private BadgeAwardRepository badgeAwardRepository;

    @InjectMocks
    private CertificationServiceImpl certificationService;

    @Test
    void issueCertification_success_createsAndReturnsCertificate() {
        // Arrange
        CertificationRequest request = CertificationRequest.builder()
                .courseId(10)
                .learnerId(100)
                .expiryDate(LocalDate.of(2027, 1, 1))
                .build();
        Certification saved = Certification.builder()
                .certificationId(1)
                .courseId(10)
                .learnerId(100)
                .issuedDate(LocalDate.now())
                .certificateNumber("LVC-ABCD1234")
                .status(CertificationStatus.VALID)
                .build();
        when(certificationRepository.findByCourseIdAndLearnerId(10, 100)).thenReturn(Optional.empty());
        when(certificationRepository.save(any(Certification.class))).thenReturn(saved);

        // Act
        CertificationResponse response = certificationService.issueCertification(request);

        // Assert
        assertEquals("LVC-ABCD1234", response.getCertificateNumber());
        assertEquals(CertificationStatus.VALID, response.getStatus());
        verify(certificationRepository).save(any(Certification.class));
    }

    @Test
    void getCertificationById_notFound_throwsResourceNotFoundException() {
        // Arrange
        when(certificationRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> certificationService.getCertificationById(99));
    }

    @Test
    void revokeCertification_success_setsStatusRevoked() {
        // Arrange
        Certification cert = Certification.builder()
                .certificationId(1)
                .courseId(10)
                .learnerId(100)
                .certificateNumber("LVC-ABCD1234")
                .status(CertificationStatus.VALID)
                .build();
        when(certificationRepository.findById(1)).thenReturn(Optional.of(cert));
        when(certificationRepository.save(any(Certification.class))).thenReturn(cert);

        // Act
        CertificationResponse response = certificationService.revokeCertification(1);

        // Assert
        assertEquals(CertificationStatus.REVOKED, response.getStatus());
        verify(certificationRepository).save(any(Certification.class));
    }
}

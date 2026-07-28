package com.learnvault.certificationbadgemanagement.controller;

import com.learnvault.certificationbadgemanagement.dto.request.CertificationRequest;
import com.learnvault.certificationbadgemanagement.dto.response.CertificationResponse;
import com.learnvault.certificationbadgemanagement.service.CertificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certifications")
@RequiredArgsConstructor
public class CertificationController {

    private final CertificationService certificationService;

    @PostMapping
    public ResponseEntity<CertificationResponse> issueCertification(@RequestBody CertificationRequest request) {
        return new ResponseEntity<>(certificationService.issueCertification(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CertificationResponse>> getCertifications(
            @RequestParam(required = false) Integer learnerId) {
        if (learnerId != null) {
            return ResponseEntity.ok(certificationService.getCertificationsByLearner(learnerId));
        }
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CertificationResponse> getCertificationById(@PathVariable Integer id) {
        return ResponseEntity.ok(certificationService.getCertificationById(id));
    }

    @PatchMapping("/{id}/revoke")
    public ResponseEntity<CertificationResponse> revokeCertification(@PathVariable Integer id) {
        return ResponseEntity.ok(certificationService.revokeCertification(id));
    }
}
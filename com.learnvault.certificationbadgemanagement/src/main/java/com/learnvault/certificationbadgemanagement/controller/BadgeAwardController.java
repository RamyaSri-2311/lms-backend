package com.learnvault.certificationbadgemanagement.controller;

import com.learnvault.certificationbadgemanagement.dto.request.BadgeAwardRequest;
import com.learnvault.certificationbadgemanagement.dto.response.BadgeAwardResponse;
import com.learnvault.certificationbadgemanagement.service.BadgeAwardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/badge-awards")
@RequiredArgsConstructor
public class BadgeAwardController {

    private final BadgeAwardService badgeAwardService;

    @PostMapping
    public ResponseEntity<BadgeAwardResponse> awardBadge(@RequestBody BadgeAwardRequest request) {
        return new ResponseEntity<>(badgeAwardService.awardBadge(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BadgeAwardResponse>> getAwardsByLearner(@RequestParam Integer learnerId) {
        return ResponseEntity.ok(badgeAwardService.getAwardsByLearner(learnerId));
    }
   
}
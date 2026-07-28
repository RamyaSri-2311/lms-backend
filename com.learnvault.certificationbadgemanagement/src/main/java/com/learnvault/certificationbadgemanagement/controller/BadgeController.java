package com.learnvault.certificationbadgemanagement.controller;

import com.learnvault.certificationbadgemanagement.dto.request.BadgeRequest;
import com.learnvault.certificationbadgemanagement.dto.response.BadgeResponse;
import com.learnvault.certificationbadgemanagement.service.BadgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/badges")
@RequiredArgsConstructor
public class BadgeController {

    private final BadgeService badgeService;

    @PostMapping
    public ResponseEntity<BadgeResponse> createBadge(@RequestBody BadgeRequest request) {
        return new ResponseEntity<>(badgeService.createBadge(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BadgeResponse>> getAllBadges() {
        return ResponseEntity.ok(badgeService.getAllBadges());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BadgeResponse> getBadgeById(@PathVariable Integer id) {
        return ResponseEntity.ok(badgeService.getBadgeById(id));
    }
   
}
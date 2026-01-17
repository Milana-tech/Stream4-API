package com.stream.four.controller;

import com.stream.four.dto.TrialResponse;
import com.stream.four.service.TrialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trial")
@RequiredArgsConstructor
@Tag(name = "Trial Management", description = "Manage 7-day trial period")
@SecurityRequirement(name = "bearerAuth")
public class TrialController {

    private final TrialService trialService;

    @GetMapping
    @Operation(summary = "Get trial status", description = "Get trial information for current user")  // ← Changed
    public ResponseEntity<TrialResponse> getTrial(Authentication authentication) {
        String userId = authentication.getName();  // ← Changed
        TrialResponse trial = trialService.getTrial(userId);  // ← Changed
        return ResponseEntity.ok(trial);
    }

    @GetMapping("/active")
    @Operation(summary = "Check if trial is active", description = "Check if user has active trial")  // ← Changed
    public ResponseEntity<Boolean> hasActiveTrial(Authentication authentication) {
        String userId = authentication.getName();  // ← Changed
        boolean hasActiveTrial = trialService.hasActiveTrial(userId);  // ← Changed
        return ResponseEntity.ok(hasActiveTrial);
    }
}
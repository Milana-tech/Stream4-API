package com.stream.four.controller.subscription;

import com.stream.four.dto.response.subscription.TrialResponse;
import com.stream.four.service.TrialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping ("/api/trial")
@RequiredArgsConstructor
@Tag (name = "Trial Management", description = "Manage 7-day trial period (supports JSON, XML, CSV)")
@SecurityRequirement (name = "bearerAuth")
public class TrialController
{

    private final TrialService trialService;

    @GetMapping (produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation (summary = "Get trial status", description = "Get trial information for current user. Supports JSON, " +
            "XML, CSV.")
    public ResponseEntity<TrialResponse> getTrial(Authentication authentication)
    {
        String userId = authentication.getName();
        TrialResponse trial = trialService.getTrial(userId);
        return ResponseEntity.ok(trial);
    }

    @GetMapping (value = "/active", produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation (summary = "Check if trial is active", description = "Check if user has active trial. Supports JSON, " +
            "XML, CSV.")
    public ResponseEntity<Boolean> hasActiveTrial(Authentication authentication)
    {
        String userId = authentication.getName();
        boolean hasActiveTrial = trialService.hasActiveTrial(userId);
        return ResponseEntity.ok(hasActiveTrial);
    }
}
package com.stream.four.controller.subscription;

import com.stream.four.dto.response.subscription.TrialResponse;
import com.stream.four.exception.ErrorResponse;
import com.stream.four.service.TrialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trials")
@RequiredArgsConstructor
@Tag(name = "trial-management", description = "Manage 7-day trial period (supports JSON, XML, CSV)")
@SecurityRequirement(name = "bearerAuth")
public class TrialController {

    private final TrialService trialService;

    @GetMapping(produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation(summary = "Get trial status", description = "Get trial information for current user. Supports JSON, XML, CSV.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trial information retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "No trial found for current user",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TrialResponse> getTrial(Authentication authentication) {
        String userId = authentication.getName();
        TrialResponse trial = trialService.getTrial(userId);
        return ResponseEntity.ok(trial);
    }

    @GetMapping(value = "/active", produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation(summary = "Check if trial is active", description = "Check if user has active trial. Supports JSON, XML, CSV.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trial status returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Boolean> hasActiveTrial(Authentication authentication) {
        String userId = authentication.getName();
        boolean hasActiveTrial = trialService.hasActiveTrial(userId);
        return ResponseEntity.ok(hasActiveTrial);
    }
}

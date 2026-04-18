package com.stream.four.controller;

import com.stream.four.dto.requests.CreateInvitationRequest;
import com.stream.four.dto.response.InvitationResponse;
import com.stream.four.exception.ErrorResponse;
import com.stream.four.service.InvitationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/invitations")
@Tag(name = "invitations", description = "Invite new users to StreamFlix")
public class InvitationController {

    private final InvitationService invitationService;

    @PostMapping(produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation(summary = "Send invitation", description = "Send an invitation link to a new user by email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Invitation sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input - validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<InvitationResponse> invite(@Valid @RequestBody CreateInvitationRequest request,
                                                      Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(invitationService.createInvitation(principal.getName(), request));
    }

    @GetMapping(produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation(operationId = "getSentInvitations", summary = "Get sent invitations", description = "Get all invitations sent by the current user, including discount status and validity period")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Invitations retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<InvitationResponse>> getSent(Principal principal) {
        return ResponseEntity.ok(invitationService.getSentInvitations(principal.getName()));
    }
}

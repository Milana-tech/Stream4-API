package com.stream.four.controller;

import com.stream.four.dto.requests.CreateInvitationRequest;
import com.stream.four.dto.response.InvitationResponse;
import com.stream.four.service.InvitationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

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
    public ResponseEntity<InvitationResponse> invite(@Valid @RequestBody CreateInvitationRequest request,
                                                      Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(invitationService.createInvitation(principal.getName(), request));
    }
}

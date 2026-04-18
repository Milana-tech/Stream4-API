package com.stream.four.service;

import com.stream.four.dto.requests.CreateInvitationRequest;
import com.stream.four.dto.response.InvitationResponse;
import com.stream.four.exception.ResourceNotFoundException;
import com.stream.four.model.Invitation;
import com.stream.four.repository.InvitationRepository;
import com.stream.four.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Value("${app.frontend.base-url}")
    private String baseUrl;

    public InvitationResponse createInvitation(String inviterUserId, CreateInvitationRequest request) {
        var inviter = userRepository.findById(inviterUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (inviter.getEmail().equalsIgnoreCase(request.getInviteeEmail())) {
            throw new IllegalArgumentException("You cannot invite yourself.");
        }

        String token = UUID.randomUUID().toString();

        Invitation invitation = new Invitation();
        invitation.setInviterUserId(inviterUserId);
        invitation.setInviteeEmail(request.getInviteeEmail());
        invitation.setToken(token);
        invitation.setUsed(false);

        invitationRepository.save(invitation);

        String link = baseUrl + "/index.html?invitationToken=" + token;
        emailService.sendInvitationEmail(request.getInviteeEmail(), link);

        return toResponse(invitation, link);
    }

    public java.util.List<InvitationResponse> getSentInvitations(String inviterUserId) {
        return invitationRepository.findByInviterUserId(inviterUserId)
                .stream()
                .map(inv -> toResponse(inv, null))
                .toList();
    }

    private InvitationResponse toResponse(Invitation inv, String link) {
        return new InvitationResponse(
                inv.getId(), link, inv.getInviteeEmail(), inv.getInviteeUserId(),
                inv.isUsed(), inv.isDiscountApplied(), inv.getDiscountAppliedAt(), inv.getDiscountEndDate()
        );
    }
}

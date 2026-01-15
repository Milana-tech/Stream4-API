package com.stream.four.mapper;

import org.springframework.stereotype.Component;

import com.stream.four.repository.InvitationRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor

public class InvitationHelper {
    private final InvitationRepository invitationRepository;
    
    public void validatedToken(String token)
    {
        var invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid invitation token"));

        if (invitation.isUsed()) {
            throw new RuntimeException("Invitation already used");
        }
    }
    
    public void linkAccounts(String token, String email)
    {
        var invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid invitation token"));
        
        invitation.setInviteeEmail(email);
        invitation.setUsed(true); 
        invitationRepository.save(invitation);
    }
}

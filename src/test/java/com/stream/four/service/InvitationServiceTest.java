package com.stream.four.service;

import com.stream.four.dto.requests.CreateInvitationRequest;
import com.stream.four.exception.ResourceNotFoundException;
import com.stream.four.model.Invitation;
import com.stream.four.model.user.User;
import com.stream.four.repository.InvitationRepository;
import com.stream.four.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvitationServiceTest {

    private final InvitationRepository invitationRepository = mock(InvitationRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final EmailService emailService = mock(EmailService.class);

    // Inject @Value field via constructor is not possible; use reflection helper
    private final InvitationService invitationService = buildService();

    private InvitationService buildService() {
        InvitationService service = new InvitationService(invitationRepository, userRepository, emailService);
        try {
            var field = InvitationService.class.getDeclaredField("baseUrl");
            field.setAccessible(true);
            field.set(service, "http://localhost:8080");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return service;
    }

    // ========== createInvitation ==========

    @Test
    void createInvitation_validInviter_savesInvitationAndSendsEmail() {
        var inviter = new User();
        inviter.setUserId("inviter1");
        inviter.setEmail("inviter@example.com");

        var request = new CreateInvitationRequest();
        request.setInviteeEmail("newuser@example.com");

        when(userRepository.findById("inviter1")).thenReturn(Optional.of(inviter));
        when(invitationRepository.save(any(Invitation.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = invitationService.createInvitation("inviter1", request);

        assertNotNull(result);
        assertEquals("newuser@example.com", result.getInviteeEmail());
        assertTrue(result.getInvitationLink().contains("/index.html?invitationToken="));
        verify(invitationRepository).save(any(Invitation.class));
        verify(emailService).sendInvitationEmail(eq("newuser@example.com"), contains("/index.html?invitationToken="));
    }

    @Test
    void createInvitation_inviterNotFound_throwsResourceNotFoundException() {
        when(userRepository.findById("inviter1")).thenReturn(Optional.empty());

        var request = new CreateInvitationRequest();
        request.setInviteeEmail("someone@example.com");

        assertThrows(ResourceNotFoundException.class,
                () -> invitationService.createInvitation("inviter1", request));

        verifyNoInteractions(invitationRepository, emailService);
    }

    @Test
    void createInvitation_validInviter_generatesUniqueTokenEachTime() {
        var inviter = new User();
        inviter.setUserId("inviter1");
        inviter.setEmail("inviter@example.com");

        var request = new CreateInvitationRequest();
        request.setInviteeEmail("someone@example.com");

        when(userRepository.findById("inviter1")).thenReturn(Optional.of(inviter));
        when(invitationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result1 = invitationService.createInvitation("inviter1", request);
        var result2 = invitationService.createInvitation("inviter1", request);

        assertNotEquals(result1.getInvitationLink(), result2.getInvitationLink());
    }
}

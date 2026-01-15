package com.stream.four.mapper;

import com.stream.four.model.Invitation;
import com.stream.four.repository.InvitationRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvitationHelperTest {

    private final InvitationRepository invitationRepository = mock(InvitationRepository.class);
    private final InvitationHelper helper = new InvitationHelper(invitationRepository);

    @Test
    void validatedToken_invalidToken_throws() {
        when(invitationRepository.findByToken("t")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> helper.validatedToken("t"));
    }

    @Test
    void validatedToken_usedInvitation_throws() {
        var inv = new Invitation();
        inv.setUsed(true);

        when(invitationRepository.findByToken("t")).thenReturn(Optional.of(inv));
        assertThrows(RuntimeException.class, () -> helper.validatedToken("t"));
    }

    @Test
    void linkAccounts_setsInviteeEmail_andMarksUsed_andSaves() {
        var inv = new Invitation();
        inv.setToken("t");
        inv.setUsed(false);

        when(invitationRepository.findByToken("t")).thenReturn(Optional.of(inv));

        helper.linkAccounts("t", "e@e.com");

        assertEquals("e@e.com", inv.getInviteeEmail());
        assertTrue(inv.isUsed());
        verify(invitationRepository).save(inv);
    }
}


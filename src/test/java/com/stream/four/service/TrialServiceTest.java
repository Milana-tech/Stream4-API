package com.stream.four.service;

import com.stream.four.exception.DuplicateResourceException;
import com.stream.four.exception.ResourceNotFoundException;
import com.stream.four.model.enums.TrialStatus;
import com.stream.four.model.subscription.Trial;
import com.stream.four.model.user.User;
import com.stream.four.repository.TrialRepository;
import com.stream.four.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrialServiceTest {

    private final TrialRepository trialRepository = mock(TrialRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);

    private final TrialService trialService = new TrialService(trialRepository, userRepository);

    // ========== createTrial ==========

    @Test
    void createTrial_newUser_returnsSavedTrialResponse() {
        var user = new User();
        user.setUserId("u1");

        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(trialRepository.existsByUser_UserId("u1")).thenReturn(false);
        when(trialRepository.save(any())).thenAnswer(inv -> {
            Trial t = inv.getArgument(0);
            t.setTrialId(1L);
            return t;
        });

        var result = trialService.createTrial("u1");

        assertNotNull(result);
        assertEquals("u1", result.getUserId());
        assertEquals("ACTIVE", result.getStatus());
        assertNotNull(result.getEndDate());
        verify(trialRepository).save(any(Trial.class));
    }

    @Test
    void createTrial_userNotFound_throwsResourceNotFoundException() {
        when(userRepository.findById("u1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> trialService.createTrial("u1"));
    }

    @Test
    void createTrial_trialAlreadyExists_throwsDuplicateResourceException() {
        var user = new User();
        user.setUserId("u1");

        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(trialRepository.existsByUser_UserId("u1")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> trialService.createTrial("u1"));
    }

    // ========== getTrial ==========

    @Test
    void getTrial_existingUser_returnsTrialResponse() {
        var user = new User();
        user.setUserId("u1");

        var trial = Trial.builder()
                .trialId(1L)
                .user(user)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(7))
                .status(TrialStatus.ACTIVE)
                .convertedToPaid(false)
                .build();

        when(trialRepository.findByUser_UserId("u1")).thenReturn(Optional.of(trial));

        var result = trialService.getTrial("u1");

        assertNotNull(result);
        assertEquals("u1", result.getUserId());
        assertEquals("ACTIVE", result.getStatus());
    }

    @Test
    void getTrial_noTrialFound_throwsResourceNotFoundException() {
        when(trialRepository.findByUser_UserId("u1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> trialService.getTrial("u1"));
    }

    // ========== hasActiveTrial ==========

    @Test
    void hasActiveTrial_activeTrialNotExpired_returnsTrue() {
        var user = new User();
        user.setUserId("u1");

        var trial = Trial.builder()
                .user(user)
                .status(TrialStatus.ACTIVE)
                .endDate(LocalDate.now().plusDays(3))
                .convertedToPaid(false)
                .build();

        when(trialRepository.findByUser_UserId("u1")).thenReturn(Optional.of(trial));

        assertTrue(trialService.hasActiveTrial("u1"));
    }

    @Test
    void hasActiveTrial_expiredTrial_returnsFalse() {
        var user = new User();
        user.setUserId("u1");

        var trial = Trial.builder()
                .user(user)
                .status(TrialStatus.ACTIVE)
                .endDate(LocalDate.now().minusDays(1))
                .convertedToPaid(false)
                .build();

        when(trialRepository.findByUser_UserId("u1")).thenReturn(Optional.of(trial));

        assertFalse(trialService.hasActiveTrial("u1"));
    }

    @Test
    void hasActiveTrial_noTrial_returnsFalse() {
        when(trialRepository.findByUser_UserId("u1")).thenReturn(Optional.empty());

        assertFalse(trialService.hasActiveTrial("u1"));
    }

    @Test
    void hasActiveTrial_convertedTrial_returnsFalse() {
        var user = new User();
        user.setUserId("u1");

        var trial = Trial.builder()
                .user(user)
                .status(TrialStatus.CONVERTED)
                .endDate(LocalDate.now().plusDays(3))
                .convertedToPaid(true)
                .build();

        when(trialRepository.findByUser_UserId("u1")).thenReturn(Optional.of(trial));

        assertFalse(trialService.hasActiveTrial("u1"));
    }

    // ========== markTrialAsConverted ==========

    @Test
    void markTrialAsConverted_activeTrial_setsConvertedStatus() {
        var user = new User();
        user.setUserId("u1");

        var trial = Trial.builder()
                .trialId(1L)
                .user(user)
                .status(TrialStatus.ACTIVE)
                .endDate(LocalDate.now().plusDays(4))
                .convertedToPaid(false)
                .build();

        when(trialRepository.findByUser_UserId("u1")).thenReturn(Optional.of(trial));
        when(trialRepository.save(trial)).thenReturn(trial);

        trialService.markTrialAsConverted("u1");

        assertEquals(TrialStatus.CONVERTED, trial.getStatus());
        assertTrue(trial.getConvertedToPaid());
        assertNotNull(trial.getConvertedDate());
        verify(trialRepository).save(trial);
    }

    @Test
    void markTrialAsConverted_noTrialFound_throwsResourceNotFoundException() {
        when(trialRepository.findByUser_UserId("u1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> trialService.markTrialAsConverted("u1"));
    }

    // ========== expireTrials ==========

    @Test
    void expireTrials_withExpiredTrials_setsStatusExpired() {
        var user = new User();
        user.setUserId("u1");

        var expiredTrial = Trial.builder()
                .trialId(1L)
                .user(user)
                .status(TrialStatus.ACTIVE)
                .endDate(LocalDate.now().minusDays(1))
                .convertedToPaid(false)
                .build();

        when(trialRepository.findByStatusAndEndDateBefore(eq(TrialStatus.ACTIVE), any(LocalDate.class)))
                .thenReturn(List.of(expiredTrial));

        trialService.expireTrials();

        assertEquals(TrialStatus.EXPIRED, expiredTrial.getStatus());
        verify(trialRepository).save(expiredTrial);
    }

    @Test
    void expireTrials_noExpiredTrials_savesNothing() {
        when(trialRepository.findByStatusAndEndDateBefore(eq(TrialStatus.ACTIVE), any(LocalDate.class)))
                .thenReturn(List.of());

        trialService.expireTrials();

        verify(trialRepository, never()).save(any());
    }
}

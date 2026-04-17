package com.stream.four.service;

import com.stream.four.exception.ResourceNotFoundException;
import com.stream.four.model.user.User;
import com.stream.four.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PasswordRecoveryUnitTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final EmailService emailService = mock(EmailService.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

    private final PasswordRecoveryService service =
            new PasswordRecoveryService(userRepository, emailService, passwordEncoder);

    @Test
    void initiateRecovery_userNotFound_throws() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.initiateRecovery("unknown@example.com"));
    }

    @Test
    void initiateRecovery_setsTokenAndSendsEmail() {
        var user = new User();
        user.setEmail("alice@example.com");
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));

        service.initiateRecovery("alice@example.com");

        assertNotNull(user.getResetToken());
        assertNotNull(user.getResetTokenExpiry());
        verify(userRepository).save(user);
        verify(emailService).sendPasswordResetEmail(eq("alice@example.com"), anyString());
    }

    @Test
    void completePasswordReset_tokenNotFound_throws() {
        when(userRepository.findByResetToken("bad")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.completePasswordReset("bad", "newPass123"));
    }

    @Test
    void completePasswordReset_expiredToken_throws() {
        var user = new User();
        user.setResetToken("tok");
        user.setResetTokenExpiry(LocalDateTime.now().minusHours(1));
        when(userRepository.findByResetToken("tok")).thenReturn(Optional.of(user));

        assertThrows(ResourceNotFoundException.class,
                () -> service.completePasswordReset("tok", "newPass123"));
    }

    @Test
    void completePasswordReset_validToken_resetsPassword() {
        var user = new User();
        user.setResetToken("tok");
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        user.setFailedLoginAttempts(3);

        when(userRepository.findByResetToken("tok")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPass123")).thenReturn("encoded");

        service.completePasswordReset("tok", "newPass123");

        assertEquals("encoded", user.getPassword());
        assertNull(user.getResetToken());
        assertNull(user.getResetTokenExpiry());
        assertEquals(0, user.getFailedLoginAttempts());
        verify(userRepository).save(user);
    }
}

package com.stream.four.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    private final JavaMailSender mailSender = mock(JavaMailSender.class);

    private final EmailService emailService = buildService();

    private EmailService buildService() {
        EmailService service = new EmailService(mailSender);
        try {
            var fromField = EmailService.class.getDeclaredField("from");
            fromField.setAccessible(true);
            fromField.set(service, "noreply@streamflix.com");

            var baseUrlField = EmailService.class.getDeclaredField("baseUrl");
            baseUrlField.setAccessible(true);
            baseUrlField.set(service, "http://localhost:8080");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return service;
    }

    // ========== sendVerificationEmail ==========

    @Test
    void sendVerificationEmail_validInputs_sendsEmailWithVerificationLink() {
        emailService.sendVerificationEmail("user@example.com", "abc123");

        var captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage sent = captor.getValue();
        assertArrayEquals(new String[]{"user@example.com"}, sent.getTo());
        assertEquals("noreply@streamflix.com", sent.getFrom());
        assertEquals("StreamFlix – Verify your account", sent.getSubject());
        assertTrue(sent.getText().contains("/auth/verify?token=abc123"));
    }

    @Test
    void sendVerificationEmail_called_invokesMailSenderExactlyOnce() {
        emailService.sendVerificationEmail("user@example.com", "token");

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    // ========== sendInvitationEmail ==========

    @Test
    void sendInvitationEmail_validInputs_sendsEmailWithInvitationLink() {
        String link = "http://localhost:8080/auth/register?invitationToken=xyz";

        emailService.sendInvitationEmail("invited@example.com", link);

        var captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage sent = captor.getValue();
        assertArrayEquals(new String[]{"invited@example.com"}, sent.getTo());
        assertEquals("StreamFlix – You've been invited!", sent.getSubject());
        assertTrue(sent.getText().contains(link));
    }

    @Test
    void sendInvitationEmail_called_invokesMailSenderExactlyOnce() {
        emailService.sendInvitationEmail("invited@example.com", "http://example.com/link");

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    // ========== sendPasswordResetEmail ==========

    @Test
    void sendPasswordResetEmail_validInputs_sendsEmailWithResetLink() {
        emailService.sendPasswordResetEmail("user@example.com", "resetToken99");

        var captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage sent = captor.getValue();
        assertArrayEquals(new String[]{"user@example.com"}, sent.getTo());
        assertEquals("StreamFlix – Password reset request", sent.getSubject());
        assertTrue(sent.getText().contains("/auth/reset-password?token=resetToken99"));
    }

    @Test
    void sendPasswordResetEmail_called_invokesMailSenderExactlyOnce() {
        emailService.sendPasswordResetEmail("user@example.com", "token");

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}

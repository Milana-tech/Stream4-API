package com.stream.four.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${app.verification.base-url}")
    private String baseUrl;

    public void sendVerificationEmail(String to, String token) {
        String link = baseUrl + "/auth/verify?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("StreamFlix – Verify your account");
        message.setText("Thank you for registering!\n\nPlease activate your account by clicking the link below:\n\n" + link + "\n\nThe link expires in 24 hours.");

        mailSender.send(message);
    }

    public void sendInvitationEmail(String to, String invitationLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("StreamFlix – You've been invited!");
        message.setText("You've been invited to join StreamFlix!\n\nClick the link below to register:\n\n"
                + invitationLink + "\n\nEnjoy your first month with a discount when you subscribe.");

        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String to, String token) {
        String link = baseUrl + "/auth/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("StreamFlix – Password reset request");
        message.setText("We received a request to reset your password.\n\nClick the link below to set a new password:\n\n" + link + "\n\nIf you did not request this, please ignore this email.");

        mailSender.send(message);
    }
}

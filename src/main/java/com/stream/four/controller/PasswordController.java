package com.stream.four.controller;

import com.stream.four.service.PasswordRecoveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class PasswordController {
    private final PasswordRecoveryService recoveryService;

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email) {
        recoveryService.initiateRecovery(email);
        return "If an account exists with that email, a recovery link has been sent.";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        recoveryService.completePasswordReset(token, newPassword);

        return "Password has been reset successfully!";
    }
}
package com.stream.four.controller.user;

import com.stream.four.dto.requests.ForgotPasswordRequest;
import com.stream.four.dto.requests.ResetPasswordRequest;
import com.stream.four.service.PasswordRecoveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "auth", description = "Endpoints for login and logout")
public class PasswordController {
    private final PasswordRecoveryService recoveryService;

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        recoveryService.initiateRecovery(request.getEmail());
        return ResponseEntity.ok("If an account exists with that email, a recovery link has been sent.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        recoveryService.completePasswordReset(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Password has been reset successfully!");
    }
}
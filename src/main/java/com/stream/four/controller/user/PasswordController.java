package com.stream.four.controller.user;

import com.stream.four.dto.requests.ForgotPasswordRequest;
import com.stream.four.dto.requests.ResetPasswordRequest;
import com.stream.four.dto.response.MessageResponse;
import com.stream.four.service.PasswordRecoveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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

    @PostMapping(value = "/forgot-password", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        recoveryService.initiateRecovery(request.getEmail());
        return ResponseEntity.ok(new MessageResponse("If an account exists with that email, a recovery link has been sent."));
    }

    @PostMapping(value = "/reset-password", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        recoveryService.completePasswordReset(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(new MessageResponse("Password has been reset successfully!"));
    }
}

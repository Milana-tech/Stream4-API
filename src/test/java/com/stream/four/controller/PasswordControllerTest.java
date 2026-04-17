package com.stream.four.controller;

import com.stream.four.controller.user.PasswordController;
import com.stream.four.exception.GlobalExceptionHandler;
import com.stream.four.exception.ResourceNotFoundException;
import com.stream.four.service.PasswordRecoveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PasswordControllerTest {

    private MockMvc mockMvc;
    private final PasswordRecoveryService recoveryService = mock(PasswordRecoveryService.class);

    @BeforeEach
    void setUp() {
        var controller = new PasswordController(recoveryService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void forgotPassword_validEmail_returns200() throws Exception {
        doNothing().when(recoveryService).initiateRecovery("alice@example.com");

        var body = """
                { "email": "alice@example.com" }
                """;

        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void forgotPassword_invalidEmail_returns400() throws Exception {
        var body = """
                { "email": "not-an-email" }
                """;

        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void resetPassword_validToken_returns200() throws Exception {
        doNothing().when(recoveryService).completePasswordReset("tok", "newPass123");

        var body = """
                { "token": "tok", "newPassword": "newPass123" }
                """;

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void resetPassword_invalidToken_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Invalid token"))
                .when(recoveryService).completePasswordReset("bad", "pass12345");

        var body = """
                { "token": "bad", "newPassword": "pass12345" }
                """;

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void resetPassword_shortPassword_returns400() throws Exception {
        var body = """
                { "token": "tok", "newPassword": "short" }
                """;

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}

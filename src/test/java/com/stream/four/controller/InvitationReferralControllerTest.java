package com.stream.four.controller;

import com.stream.four.controller.referral.ReferralController;
import com.stream.four.dto.response.InvitationResponse;
import com.stream.four.dto.response.referral.ReferralDiscountResponse;
import com.stream.four.exception.GlobalExceptionHandler;
import com.stream.four.service.InvitationService;
import com.stream.four.service.SubscriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class InvitationReferralControllerTest {

    private MockMvc invitationMvc;
    private MockMvc referralMvc;

    private final InvitationService invitationService = mock(InvitationService.class);
    private final SubscriptionService subscriptionService = mock(SubscriptionService.class);

    @BeforeEach
    void setUp() {
        invitationMvc = MockMvcBuilders
                .standaloneSetup(new InvitationController(invitationService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        referralMvc = MockMvcBuilders
                .standaloneSetup(new ReferralController(subscriptionService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private UsernamePasswordAuthenticationToken principal() {
        return new UsernamePasswordAuthenticationToken("user-001", null, List.of());
    }

    // --- Invitation ---

    @Test
    void invite_valid_returns201() throws Exception {
        var response = new InvitationResponse("http://link", "friend@example.com");
        when(invitationService.createInvitation(eq("user-001"), any())).thenReturn(response);

        invitationMvc.perform(post("/invitations")
                        .principal(principal())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"inviteeEmail\": \"friend@example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.inviteeEmail").value("friend@example.com"));
    }

    @Test
    void invite_invalidEmail_returns400() throws Exception {
        invitationMvc.perform(post("/invitations")
                        .principal(principal())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"inviteeEmail\": \"not-an-email\"}"))
                .andExpect(status().isBadRequest());
    }

    // --- Referral ---

    @Test
    void applyReferralDiscount_returns200() throws Exception {
        var response = new ReferralDiscountResponse("u1", "u2", true, true, "Applied");
        when(subscriptionService.applyReferralDiscount("u2")).thenReturn(response);

        referralMvc.perform(post("/referrals/apply-discount/u2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Applied"));
    }
}

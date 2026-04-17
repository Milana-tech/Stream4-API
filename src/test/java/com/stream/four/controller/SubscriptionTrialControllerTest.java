package com.stream.four.controller;

import com.stream.four.controller.subscription.SubscriptionController;
import com.stream.four.controller.subscription.TrialController;
import com.stream.four.dto.response.subscription.SubscriptionResponse;
import com.stream.four.dto.response.subscription.TrialResponse;
import com.stream.four.exception.GlobalExceptionHandler;
import com.stream.four.exception.ResourceNotFoundException;
import com.stream.four.service.SubscriptionService;
import com.stream.four.service.TrialService;
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

class SubscriptionTrialControllerTest {

    private MockMvc subMvc;
    private MockMvc trialMvc;

    private final SubscriptionService subscriptionService = mock(SubscriptionService.class);
    private final TrialService trialService = mock(TrialService.class);

    @BeforeEach
    void setUp() {
        subMvc = MockMvcBuilders
                .standaloneSetup(new SubscriptionController(subscriptionService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        trialMvc = MockMvcBuilders
                .standaloneSetup(new TrialController(trialService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private UsernamePasswordAuthenticationToken auth() {
        return new UsernamePasswordAuthenticationToken("user-001", null, List.of());
    }

    private SubscriptionResponse sampleSub() {
        return SubscriptionResponse.builder()
                .userId("user-001")
                .plan("HD")
                .status("ACTIVE")
                .build();
    }

    // --- Subscription ---

    @Test
    void createSubscription_valid_returns201() throws Exception {
        when(subscriptionService.createSubscription(eq("user-001"), any())).thenReturn(sampleSub());

        mockMvc(subMvc, post("/subscriptions")
                .principal(auth())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"plan\": \"HD\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.plan").value("HD"));
    }

    @Test
    void getCurrentSubscription_returns200() throws Exception {
        when(subscriptionService.getCurrentSubscription("user-001")).thenReturn(sampleSub());

        mockMvc(subMvc, get("/subscriptions/current")
                .principal(auth())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void getCurrentSubscription_notFound_returns404() throws Exception {
        when(subscriptionService.getCurrentSubscription("user-001"))
                .thenThrow(new ResourceNotFoundException("No subscription"));

        mockMvc(subMvc, get("/subscriptions/current")
                .principal(auth())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getHistory_returnsList() throws Exception {
        when(subscriptionService.getSubscriptionHistory("user-001")).thenReturn(List.of(sampleSub()));

        mockMvc(subMvc, get("/subscriptions/history")
                .principal(auth())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void cancelSubscription_returns200() throws Exception {
        when(subscriptionService.cancelSubscription("user-001")).thenReturn(sampleSub());

        mockMvc(subMvc, delete("/subscriptions/cancel")
                .principal(auth())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // --- Trial ---

    @Test
    void getTrial_returns200() throws Exception {
        var trial = TrialResponse.builder().userId("user-001").build();
        when(trialService.getTrial("user-001")).thenReturn(trial);

        mockMvc(trialMvc, get("/trials")
                .principal(auth())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void hasActiveTrial_returnsBoolean() throws Exception {
        when(trialService.hasActiveTrial("user-001")).thenReturn(true);

        mockMvc(trialMvc, get("/trials/active")
                .principal(auth())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    // Helper to reduce boilerplate
    private org.springframework.test.web.servlet.ResultActions mockMvc(
            MockMvc mvc, org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder builder) throws Exception {
        return mvc.perform(builder);
    }
}

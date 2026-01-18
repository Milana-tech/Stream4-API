package com.stream.four.controller.subscription;

import com.stream.four.dto.requests.CreateSubscriptionRequest;
import com.stream.four.dto.response.subscription.SubscriptionResponse;
import com.stream.four.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscription Management", description = "Manage paid subscriptions")
@SecurityRequirement(name = "bearerAuth")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    @Operation(summary = "Create subscription", description = "Create a paid subscription (converts trial if active)")
    public ResponseEntity<SubscriptionResponse> createSubscription(
            @Valid @RequestBody CreateSubscriptionRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        SubscriptionResponse subscription = subscriptionService.createSubscription(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(subscription);
    }

    @GetMapping("/current")
    @Operation(summary = "Get current subscription", description = "Get active subscription for current user")
    public ResponseEntity<SubscriptionResponse> getCurrentSubscription(Authentication authentication) {
        String userId = authentication.getName();
        SubscriptionResponse subscription = subscriptionService.getCurrentSubscription(userId);
        return ResponseEntity.ok(subscription);
    }

    @GetMapping("/history")
    @Operation(summary = "Get subscription history", description = "Get all subscriptions for current user")
    public ResponseEntity<List<SubscriptionResponse>> getHistory(Authentication authentication) {
        String userId = authentication.getName();
        List<SubscriptionResponse> subscriptions = subscriptionService.getSubscriptionHistory(userId);
        return ResponseEntity.ok(subscriptions);
    }

    @DeleteMapping("/cancel")
    @Operation(summary = "Cancel subscription", description = "Cancel current subscription")
    public ResponseEntity<SubscriptionResponse> cancelSubscription(Authentication authentication) {
        String userId = authentication.getName();
        SubscriptionResponse subscription = subscriptionService.cancelSubscription(userId);
        return ResponseEntity.ok(subscription);
    }
}
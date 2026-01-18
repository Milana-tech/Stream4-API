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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("/api/subscriptions")
@RequiredArgsConstructor
@Tag (name = "Subscription Management", description = "Manage paid subscriptions (supports JSON, XML, CSV)")
@SecurityRequirement (name = "bearerAuth")
public class SubscriptionController
{

    private final SubscriptionService subscriptionService;

    @PostMapping (produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation (summary = "Create subscription", description = "Create a paid subscription (converts trial if active)" +
            ". Supports JSON, XML, CSV.")
    public ResponseEntity<SubscriptionResponse> createSubscription(@Valid @RequestBody CreateSubscriptionRequest request, Authentication authentication)
    {
        String userId = authentication.getName();
        SubscriptionResponse subscription = subscriptionService.createSubscription(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(subscription);
    }

    @GetMapping (value = "/current", produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation (summary = "Get current subscription", description = "Get active subscription for current user. " +
            "Supports JSON, XML, CSV.")
    public ResponseEntity<SubscriptionResponse> getCurrentSubscription(Authentication authentication)
    {
        String userId = authentication.getName();
        SubscriptionResponse subscription = subscriptionService.getCurrentSubscription(userId);
        return ResponseEntity.ok(subscription);
    }

    @GetMapping (value = "/history", produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation (summary = "Get subscription history", description = "Get all subscriptions for current user. Supports" +
            " JSON, XML, CSV.")
    public ResponseEntity<List<SubscriptionResponse>> getHistory(Authentication authentication)
    {
        String userId = authentication.getName();
        List<SubscriptionResponse> subscriptions = subscriptionService.getSubscriptionHistory(userId);
        return ResponseEntity.ok(subscriptions);
    }

    @DeleteMapping (value = "/cancel", produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation (summary = "Cancel subscription", description = "Cancel current subscription. Supports JSON, XML, CSV.")
    public ResponseEntity<SubscriptionResponse> cancelSubscription(Authentication authentication)
    {
        String userId = authentication.getName();
        SubscriptionResponse subscription = subscriptionService.cancelSubscription(userId);
        return ResponseEntity.ok(subscription);
    }
}
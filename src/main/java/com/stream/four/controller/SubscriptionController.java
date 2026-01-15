package com.stream.four.controller;

import com.stream.four.dto.CreateSubscriptionRequest;
import com.stream.four.dto.SubscriptionResponse;
import com.stream.four.dto.TrialResponse;
import com.stream.four.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/subscriptions")
    public SubscriptionResponse subscribe(@Valid @RequestBody CreateSubscriptionRequest request, Principal principal) {
        return subscriptionService.subscribe(principal.getName(), request);
    }

    @GetMapping("/subscriptions")
    public SubscriptionResponse getSubscription(Principal principal) {
        return subscriptionService.getActiveSubscription(principal.getName());
    }

    @GetMapping("/trials")
    public TrialResponse getTrial(Principal principal) {
        return subscriptionService.getTrial(principal.getName());
    }
}

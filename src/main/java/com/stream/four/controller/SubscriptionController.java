package com.stream.four.controller;

import com.stream.four.dto.CreateSubscriptionRequest;
import com.stream.four.dto.SubscriptionResponse;
import com.stream.four.dto.TrialResponse;
import com.stream.four.mapper.SubscriptionMapper;
import com.stream.four.mapper.TrialMapper;
import com.stream.four.repository.SubscriptionRepository;
import com.stream.four.repository.TrialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class SubscriptionController {

    private final SubscriptionRepository subscriptionRepository;
    private final TrialRepository trialRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final TrialMapper trialMapper;

    @PostMapping("/subscriptions")
    public SubscriptionResponse subscribe(@RequestBody CreateSubscriptionRequest request, Principal principal) {

        var userId = principal.getName();

        subscriptionRepository.findByUserIdAndActiveTrue(userId)
                .ifPresent(s -> {
                    s.setActive(false);
                    subscriptionRepository.save(s);
                });

        var subscription = subscriptionMapper.toEntity(request);
        subscription.setUserId(userId);
        subscription.setStartDate(System.currentTimeMillis());
        subscription.setEndDate(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);
        subscription.setActive(true);

        subscriptionRepository.save(subscription);

        return subscriptionMapper.toDto(subscription);
    }

    @GetMapping("/subscriptions")
    public SubscriptionResponse getSubscription(Principal principal) {
        var subscription = subscriptionRepository.findByUserIdAndActiveTrue(principal.getName())
                .orElseThrow(() -> new RuntimeException("No active subscription"));

        return subscriptionMapper.toDto(subscription);
    }

    @GetMapping("/trials")
    public TrialResponse getTrial(Principal principal) {
        var trial = trialRepository.findByUserId(principal.getName())
                .orElseThrow(() -> new RuntimeException("No trial found"));
                
        return trialMapper.toDto(trial);
    }
}

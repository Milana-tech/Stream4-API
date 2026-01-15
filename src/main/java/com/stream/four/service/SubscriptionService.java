package com.stream.four.service;

import com.stream.four.dto.CreateSubscriptionRequest;
import com.stream.four.dto.SubscriptionResponse;
import com.stream.four.dto.TrialResponse;
import com.stream.four.mapper.SubscriptionMapper;
import com.stream.four.mapper.TrialMapper;
import com.stream.four.repository.SubscriptionRepository;
import com.stream.four.repository.TrialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final TrialRepository trialRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final TrialMapper trialMapper;

    public SubscriptionResponse subscribe(String userId, CreateSubscriptionRequest request) {
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

    public SubscriptionResponse getActiveSubscription(String userId) {
        var subscription = subscriptionRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new RuntimeException("No active subscription"));
        return subscriptionMapper.toDto(subscription);
    }

    public TrialResponse getTrial(String userId) {
        var trial = trialRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("No trial found"));
        return trialMapper.toDto(trial);
    }
}


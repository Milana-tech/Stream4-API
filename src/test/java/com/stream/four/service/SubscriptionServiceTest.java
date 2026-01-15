package com.stream.four.service;

import com.stream.four.dto.CreateSubscriptionRequest;
import com.stream.four.dto.SubscriptionResponse;
import com.stream.four.dto.TrialResponse;
import com.stream.four.mapper.SubscriptionMapper;
import com.stream.four.mapper.TrialMapper;
import com.stream.four.model.Subscription;
import com.stream.four.model.Trial;
import com.stream.four.repository.SubscriptionRepository;
import com.stream.four.repository.TrialRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubscriptionServiceTest {

    private final SubscriptionRepository subscriptionRepository = mock(SubscriptionRepository.class);
    private final TrialRepository trialRepository = mock(TrialRepository.class);
    private final SubscriptionMapper subscriptionMapper = mock(SubscriptionMapper.class);
    private final TrialMapper trialMapper = mock(TrialMapper.class);

    private final SubscriptionService subscriptionService = new SubscriptionService(subscriptionRepository, trialRepository, subscriptionMapper, trialMapper);

    @Test
    void subscribe_deactivatesExistingActiveSubscription() {
        var existing = new Subscription();
        existing.setActive(true);

        when(subscriptionRepository.findByUserIdAndActiveTrue("u")).thenReturn(Optional.of(existing));
        when(subscriptionMapper.toEntity(any(CreateSubscriptionRequest.class))).thenReturn(new Subscription());
        when(subscriptionMapper.toDto(any(Subscription.class))).thenReturn(new SubscriptionResponse());

        subscriptionService.subscribe("u", new CreateSubscriptionRequest());

        assertFalse(existing.isActive());
        verify(subscriptionRepository).save(existing);
    }

    @Test
    void getActiveSubscription_missing_throws() {
        when(subscriptionRepository.findByUserIdAndActiveTrue("u")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> subscriptionService.getActiveSubscription("u"));
    }

    @Test
    void getTrial_mapsToDto() {
        var trial = new Trial();
        var dto = new TrialResponse();

        when(trialRepository.findByUserId("u")).thenReturn(Optional.of(trial));
        when(trialMapper.toDto(trial)).thenReturn(dto);

        assertSame(dto, subscriptionService.getTrial("u"));
    }
}


package com.stream.four.service;

import com.stream.four.dto.response.subscription.SubscriptionResponse;
import com.stream.four.exception.ResourceNotFoundException;
import com.stream.four.model.enums.SubscriptionStatus;
import com.stream.four.model.enums.SubscriptionPlan;
import com.stream.four.model.subscription.Subscription;
import com.stream.four.model.user.User;
import com.stream.four.repository.InvitationRepository;
import com.stream.four.repository.SubscriptionRepository;
import com.stream.four.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubscriptionServiceTest {

    private final SubscriptionRepository subscriptionRepository = mock(SubscriptionRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final TrialService trialService = mock(TrialService.class);
    private final InvitationRepository invitationRepository = mock(InvitationRepository.class);

    private final SubscriptionService subscriptionService =
            new SubscriptionService(subscriptionRepository, userRepository, trialService, invitationRepository);

    @Test
    void getCurrentSubscription_existing_returnsResponse() {
        var user = new User();
        user.setUserId("u");

        var sub = Subscription.builder()
                .subscriptionId(1L)
                .user(user)
                .status(SubscriptionStatus.ACTIVE)
                .totalPrice(BigDecimal.TEN)
                .discountPercentage(BigDecimal.ZERO)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(1))
                .autoRenew(true)
                .plan(SubscriptionPlan.SD)
                .build();

        when(subscriptionRepository.findByUser_UserIdAndStatus("u", SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(sub));

        SubscriptionResponse result = subscriptionService.getCurrentSubscription("u");

        assertNotNull(result);
        assertEquals("u", result.getUserId());
    }

    @Test
    void getCurrentSubscription_missing_throws() {
        when(subscriptionRepository.findByUser_UserIdAndStatus("u", SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> subscriptionService.getCurrentSubscription("u"));
    }

    @Test
    void cancelSubscription_setsStatusCancelled() {
        var user = new User();
        user.setUserId("u");

        var sub = Subscription.builder()
                .subscriptionId(1L)
                .user(user)
                .status(SubscriptionStatus.ACTIVE)
                .totalPrice(BigDecimal.TEN)
                .discountPercentage(BigDecimal.ZERO)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(1))
                .autoRenew(true)
                .plan(SubscriptionPlan.SD)
                .build();

        when(subscriptionRepository.findByUser_UserIdAndStatus("u", SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(sub));
        when(subscriptionRepository.save(sub)).thenReturn(sub);

        subscriptionService.cancelSubscription("u");

        assertEquals(SubscriptionStatus.CANCELLED, sub.getStatus());
        assertFalse(sub.getAutoRenew());
        verify(subscriptionRepository).save(sub);
    }

    @Test
    void getSubscriptionHistory_returnsMappedList() {
        var user = new User();
        user.setUserId("u");

        var sub = Subscription.builder()
                .subscriptionId(1L)
                .user(user)
                .status(SubscriptionStatus.ACTIVE)
                .totalPrice(BigDecimal.TEN)
                .discountPercentage(BigDecimal.ZERO)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(1))
                .autoRenew(true)
                .plan(SubscriptionPlan.SD)
                .build();

        when(subscriptionRepository.findByUser_UserId("u")).thenReturn(List.of(sub));

        var result = subscriptionService.getSubscriptionHistory("u");

        assertEquals(1, result.size());
        verify(subscriptionRepository).findByUser_UserId("u");
    }
}

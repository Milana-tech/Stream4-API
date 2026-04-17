package com.stream.four.service;

import com.stream.four.dto.requests.CreateSubscriptionRequest;
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

    @Test
    void createSubscription_withReferral_appliesDiscountToBothParties() {
        // Arrange
        var inviter = new User();
        inviter.setUserId("inviter1");
        inviter.setReferralDiscountUsed(false);

        var invitee = new User();
        invitee.setUserId("invitee1");
        invitee.setInvitedBy("inviter1");
        invitee.setReferralDiscountUsed(false);

        var inviterSub = new Subscription();
        inviterSub.setStatus(SubscriptionStatus.ACTIVE);
        inviterSub.setTotalPrice(new BigDecimal("12.99"));
        inviterSub.setUser(inviter);

        var request = new CreateSubscriptionRequest();
        request.setPlan(SubscriptionPlan.HD);

        when(userRepository.findById("invitee1")).thenReturn(Optional.of(invitee));
        when(userRepository.findById("inviter1")).thenReturn(Optional.of(inviter));
        when(subscriptionRepository.existsByUser_UserIdAndStatus("invitee1", SubscriptionStatus.ACTIVE)).thenReturn(false);
        when(subscriptionRepository.findByUser_UserIdAndStatus("inviter1", SubscriptionStatus.ACTIVE)).thenReturn(Optional.of(inviterSub));
        when(subscriptionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(invitationRepository.findByInviteeUserId("invitee1")).thenReturn(Optional.empty());

        // Act
        var result = subscriptionService.createSubscription("invitee1", request);

        // Assert
        assertEquals(new BigDecimal("10.00"), result.getDiscountPercentage());
        assertTrue(invitee.isReferralDiscountUsed());
        assertTrue(inviter.isReferralDiscountUsed());
    }

    @Test
    void createSubscription_discountAlreadyUsed_doesNotApplyDiscount() {
        // Arrange
        var invitee = new User();
        invitee.setUserId("invitee1");
        invitee.setInvitedBy("inviter1");
        invitee.setReferralDiscountUsed(true); // already used

        var request = new CreateSubscriptionRequest();
        request.setPlan(SubscriptionPlan.HD);

        when(userRepository.findById("invitee1")).thenReturn(Optional.of(invitee));
        when(subscriptionRepository.existsByUser_UserIdAndStatus("invitee1", SubscriptionStatus.ACTIVE)).thenReturn(false);
        when(subscriptionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        var result = subscriptionService.createSubscription("invitee1", request);

        // Assert
        assertNull(result.getDiscountPercentage());
    }

    @Test
    void createSubscription_noReferral_createsBasicSubscription() {
        var user = new User();
        user.setUserId("u1");

        var request = new CreateSubscriptionRequest();
        request.setPlan(SubscriptionPlan.HD);

        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(subscriptionRepository.existsByUser_UserIdAndStatus("u1", SubscriptionStatus.ACTIVE)).thenReturn(false);
        when(subscriptionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = subscriptionService.createSubscription("u1", request);

        assertNotNull(result);
        verify(subscriptionRepository).save(any());
    }

    @Test
    void createSubscription_alreadyActive_throws() {
        when(userRepository.findById("u1")).thenReturn(Optional.of(new User()));
        when(subscriptionRepository.existsByUser_UserIdAndStatus("u1", SubscriptionStatus.ACTIVE)).thenReturn(true);

        var request = new CreateSubscriptionRequest();
        request.setPlan(SubscriptionPlan.HD);

        assertThrows(com.stream.four.exception.DuplicateResourceException.class,
                () -> subscriptionService.createSubscription("u1", request));
    }
}

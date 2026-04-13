package com.stream.four.service;

import com.stream.four.dto.requests.CreateSubscriptionRequest;
import com.stream.four.dto.response.subscription.SubscriptionResponse;
import com.stream.four.exception.DuplicateResourceException;
import com.stream.four.exception.ResourceNotFoundException;
import com.stream.four.model.enums.SubscriptionPlan;
import com.stream.four.model.enums.SubscriptionStatus;
import com.stream.four.model.subscription.Subscription;
import com.stream.four.model.user.User;
import com.stream.four.repository.InvitationRepository;
import com.stream.four.repository.SubscriptionRepository;
import com.stream.four.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionService {

    private static final BigDecimal REFERRAL_DISCOUNT_PERCENTAGE = new BigDecimal("10.00");

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final TrialService trialService;
    private final InvitationRepository invitationRepository;

    public SubscriptionResponse createSubscription(String userId, CreateSubscriptionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (subscriptionRepository.existsByUser_UserIdAndStatus(userId, SubscriptionStatus.ACTIVE)) {
            throw new DuplicateResourceException("User already has an active subscription");
        }

        SubscriptionPlan plan = request.getPlan();

        Subscription subscription = Subscription.builder()
                .user(user)
                .status(SubscriptionStatus.ACTIVE)
                .plan(plan.name())
                .totalPrice(plan.getMonthlyPrice())
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(1))
                .autoRenew(true)
                .build();

        // Auto-apply referral discount if user was invited and discount not yet used
        if (user.getInvitedBy() != null && !user.isReferralDiscountUsed()) {
            User inviter = userRepository.findById(user.getInvitedBy()).orElse(null);
            if (inviter != null && !inviter.isReferralDiscountUsed()) {
                applyReferralDiscount(subscription, user, inviter);
            }
        }

        Subscription saved = subscriptionRepository.save(subscription);

        if (trialService.hasActiveTrial(userId)) {
            trialService.markTrialAsConverted(userId);
        }

        return toSubscriptionResponse(saved);
    }

    @Transactional(readOnly = true)
    public SubscriptionResponse getCurrentSubscription(String userId) {
        Subscription subscription = subscriptionRepository
                .findByUser_UserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription found"));

        return toSubscriptionResponse(subscription);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionResponse> getSubscriptionHistory(String userId) {
        return subscriptionRepository.findByUser_UserId(userId)
                .stream()
                .map(this::toSubscriptionResponse)
                .collect(Collectors.toList());
    }

    public SubscriptionResponse cancelSubscription(String userId) {
        Subscription subscription = subscriptionRepository
                .findByUser_UserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription found"));

        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription.setAutoRenew(false);

        return toSubscriptionResponse(subscriptionRepository.save(subscription));
    }

    // ========== PRIVATE HELPERS ==========

    private void applyReferralDiscount(Subscription newSubscription, User invitee, User inviter) {
        // Apply discount to the new subscription
        newSubscription.setReferralDiscountApplied(true);
        newSubscription.setDiscountPercentage(REFERRAL_DISCOUNT_PERCENTAGE);
        newSubscription.setDiscountEndDate(LocalDate.now().plusMonths(1));

        // Apply discount to inviter's active subscription if they have one
        subscriptionRepository.findByUser_UserIdAndStatus(inviter.getUserId(), SubscriptionStatus.ACTIVE)
                .ifPresent(inviterSub -> {
                    inviterSub.setReferralDiscountApplied(true);
                    inviterSub.setDiscountPercentage(REFERRAL_DISCOUNT_PERCENTAGE);
                    inviterSub.setDiscountEndDate(LocalDate.now().plusMonths(1));
                    inviterSub.setReferralDiscountUsed(true);
                    subscriptionRepository.save(inviterSub);
                });

        // Mark both accounts as having used the discount
        invitee.setReferralDiscountUsed(true);
        inviter.setReferralDiscountUsed(true);
        userRepository.save(inviter);

        // Record discount status centrally on the invitation
        invitationRepository.findByInviteeUserId(invitee.getUserId())
                .ifPresent(invitation -> {
                    invitation.setDiscountApplied(true);
                    invitation.setDiscountAppliedAt(LocalDate.now());
                    invitationRepository.save(invitation);
                });
    }

    private SubscriptionResponse toSubscriptionResponse(Subscription subscription) {
        BigDecimal discountedPrice = subscription.getTotalPrice();

        if (subscription.getDiscountPercentage() != null &&
                subscription.getDiscountPercentage().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discount = subscription.getTotalPrice()
                    .multiply(subscription.getDiscountPercentage())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            discountedPrice = subscription.getTotalPrice().subtract(discount);
        }

        Integer daysRemaining = null;
        if (subscription.getEndDate() != null) {
            daysRemaining = (int) ChronoUnit.DAYS.between(LocalDate.now(), subscription.getEndDate());
        }

        return SubscriptionResponse.builder()
                .subscriptionId(subscription.getSubscriptionId())
                .userId(subscription.getUser().getUserId())
                .totalPrice(subscription.getTotalPrice())
                .discountPercentage(subscription.getDiscountPercentage())
                .discountedPrice(discountedPrice)
                .discountEndDate(subscription.getDiscountEndDate())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .autoRenew(subscription.getAutoRenew())
                .daysRemaining(daysRemaining)
                .build();
    }
}

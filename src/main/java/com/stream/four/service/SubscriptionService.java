package com.stream.four.service;

import com.stream.four.dto.requests.CreateSubscriptionRequest;
import com.stream.four.dto.response.subscription.SubscriptionResponse;
import com.stream.four.exception.DuplicateResourceException;
import com.stream.four.exception.ResourceNotFoundException;
import com.stream.four.model.enums.SubscriptionStatus;
import com.stream.four.model.subscription.Subscription;
import com.stream.four.repository.SubscriptionRepository;
import com.stream.four.model.user.User;
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

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final TrialService trialService;

    /**
     * Create paid subscription
     * Can be called after trial or directly
     */
    public SubscriptionResponse createSubscription(String userId, CreateSubscriptionRequest request) {
        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if user already has an active subscription
        if (subscriptionRepository.existsByUser_UserIdAndStatus(userId, SubscriptionStatus.ACTIVE)) {
            throw new DuplicateResourceException("User already has an active subscription");
        }

        // Create subscription
        Subscription subscription = Subscription.builder()
                .user(user)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(1))
                .autoRenew(true)
                .build();

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        // If user had trial, mark it as converted
        if (trialService.hasActiveTrial(userId)) {  // ← Changed
            trialService.markTrialAsConverted(userId);
        }

        return toSubscriptionResponse(savedSubscription);
    }

    /**
     * Get current subscription for user
     */
    @Transactional(readOnly = true)
    public SubscriptionResponse getCurrentSubscription(String userId) {  // ← Changed
        Subscription subscription = subscriptionRepository
                .findByUser_UserIdAndStatus(userId, SubscriptionStatus.ACTIVE)  // ← Changed
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription found"));

        return toSubscriptionResponse(subscription);
    }

    /**
     * Get all subscription history for user
     */
    @Transactional(readOnly = true)
    public List<SubscriptionResponse> getSubscriptionHistory(String userId) {  // ← Changed
        List<Subscription> subscriptions = subscriptionRepository.findByUser_UserId(userId);  // ← Changed

        return subscriptions.stream()
                .map(this::toSubscriptionResponse)
                .collect(Collectors.toList());
    }

    /**
     * Cancel subscription
     */
    public SubscriptionResponse cancelSubscription(String userId) {  // ← Changed
        Subscription subscription = subscriptionRepository
                .findByUser_UserIdAndStatus(userId, SubscriptionStatus.ACTIVE)  // ← Changed
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription found"));

        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription.setAutoRenew(false);

        Subscription cancelledSubscription = subscriptionRepository.save(subscription);

        return toSubscriptionResponse(cancelledSubscription);
    }

    /**
     * Upgrade/Downgrade subscription
     */
    public SubscriptionResponse changeSubscriptionPackage(String userId, Long newPackageId) {  // ← Changed
        Subscription subscription = subscriptionRepository
                .findByUser_UserIdAndStatus(userId, SubscriptionStatus.ACTIVE)  // ← Changed
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription found"));

        Subscription updatedSubscription = subscriptionRepository.save(subscription);

        return toSubscriptionResponse(updatedSubscription);
    }

    // ========== HELPER METHODS ==========

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
                .userId(subscription.getUser().getId())
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
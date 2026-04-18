package com.stream.four.service;

import com.stream.four.dto.requests.CreateSubscriptionRequest;
import com.stream.four.dto.response.referral.ReferralDiscountResponse;
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
import org.springframework.transaction.annotation.Isolation;
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

    /**
     * Creates a new subscription for the given user.
     *
     * Isolation level: REPEATABLE_READ
     *
     * This method performs several reads before writing:
     *   1. Checks whether the user already has an active subscription.
     *   2. Reads the inviter's subscription and referral-discount status.
     *   3. Checks whether an active trial exists before marking it as converted.
     *
     * With READ_COMMITTED, a concurrent transaction could create a second active
     * subscription between our existence check (step 1) and our insert, bypassing
     * the duplicate guard. REPEATABLE_READ ensures that any row read once in this
     * transaction returns the same data if read again, so the check and the insert
     * see a consistent snapshot. SERIALIZABLE would add unnecessary range-lock
     * overhead that this operation does not require.
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SubscriptionResponse createSubscription(String userId, CreateSubscriptionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (subscriptionRepository.existsByUser_UserIdAndStatus(userId, SubscriptionStatus.ACTIVE)) {
            throw new DuplicateResourceException("User already has an active subscription");
        }

        SubscriptionPlan plan = request.getPlan();

        // Reuse existing cancelled/expired record if present (UNIQUE constraint on userid)
        Subscription subscription = subscriptionRepository.findByUser_UserId(userId)
                .stream().findFirst()
                .orElseGet(() -> Subscription.builder().user(user).build());

        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setPlan(plan);
        subscription.setTotalPrice(plan.getMonthlyPrice());
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(1));
        subscription.setAutoRenew(true);
        subscription.setDiscountPercentage(null);
        subscription.setDiscountEndDate(null);
        subscription.setReferralDiscountApplied(false);
        subscription.setReferralDiscountUsed(false);

        // Auto-apply referral discount if user was invited and discount not yet used
        if (user.getInvitedBy() != null && !user.isReferralDiscountUsed()) {
            User inviter = userRepository.findById(user.getInvitedBy()).orElse(null);
            if (inviter != null && !inviter.isReferralDiscountUsed()) {
                applyReferralDiscount(subscription, user, inviter);
            }
        }

        // Deferred inviter discount: invitee already subscribed before inviter did
        if (!user.isReferralDiscountUsed()) {
            invitationRepository.findByInviterUserId(userId).stream()
                    .filter(inv -> inv.isDiscountApplied() && inv.getInviteeUserId() != null)
                    .findFirst()
                    .ifPresent(inv -> {
                        subscription.setReferralDiscountApplied(true);
                        subscription.setDiscountPercentage(REFERRAL_DISCOUNT_PERCENTAGE);
                        subscription.setDiscountEndDate(LocalDate.now().plusMonths(1));
                        subscription.setReferralDiscountUsed(true);
                        user.setReferralDiscountUsed(true);
                        userRepository.save(user);
                    });
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

    // ========== REFERRAL ==========

    public ReferralDiscountResponse applyReferralDiscount(String inviteeId) {
        var invitee = userRepository.findById(inviteeId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitee not found"));

        if (invitee.getInvitedBy() == null) {
            return new ReferralDiscountResponse(null, invitee.getUserId(), false, false,
                    "Invitee was not invited by anyone.");
        }

        var inviter = userRepository.findById(invitee.getInvitedBy())
                .orElseThrow(() -> new ResourceNotFoundException("Inviter not found"));

        if (invitee.isReferralDiscountUsed() || inviter.isReferralDiscountUsed()) {
            return new ReferralDiscountResponse(inviter.getUserId(), invitee.getUserId(), false, false,
                    "Discount already used by one of the accounts.");
        }

        var inviteeSub = subscriptionRepository.findByUser_UserIdAndStatus(inviteeId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Invitee has no active subscription"));

        applyReferralDiscount(inviteeSub, invitee, inviter);

        return new ReferralDiscountResponse(inviter.getUserId(), invitee.getUserId(), true, true,
                "Referral discount applied to both users.");
    }

    // ========== PRIVATE HELPERS ==========

    private void applyReferralDiscount(Subscription inviteeSub, User invitee, User inviter) {
        inviteeSub.setReferralDiscountApplied(true);
        inviteeSub.setDiscountPercentage(REFERRAL_DISCOUNT_PERCENTAGE);
        inviteeSub.setDiscountEndDate(LocalDate.now().plusMonths(1));
        inviteeSub.setReferralDiscountUsed(true);
        invitee.setReferralDiscountUsed(true);
        userRepository.save(invitee);

        boolean inviterGotDiscount = subscriptionRepository
                .findByUser_UserIdAndStatus(inviter.getUserId(), SubscriptionStatus.ACTIVE)
                .map(inviterSub -> {
                    inviterSub.setReferralDiscountApplied(true);
                    inviterSub.setDiscountPercentage(REFERRAL_DISCOUNT_PERCENTAGE);
                    inviterSub.setDiscountEndDate(LocalDate.now().plusMonths(1));
                    inviterSub.setReferralDiscountUsed(true);
                    subscriptionRepository.save(inviterSub);
                    inviter.setReferralDiscountUsed(true);
                    userRepository.save(inviter);
                    return true;
                })
                .orElse(false);

        invitationRepository.findByInviteeUserId(invitee.getUserId())
                .ifPresent(invitation -> {
                    invitation.setDiscountApplied(true);
                    invitation.setDiscountAppliedAt(LocalDate.now());
                    invitation.setDiscountEndDate(LocalDate.now().plusMonths(1));
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
                .status(subscription.getStatus() != null ? subscription.getStatus().name() : null)
                .plan(subscription.getPlan().name())
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

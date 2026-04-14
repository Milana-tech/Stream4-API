package com.stream.four.service;

import com.stream.four.dto.response.referral.ReferralDiscountResponse;
import com.stream.four.exception.ResourceNotFoundException;
import com.stream.four.model.enums.SubscriptionStatus;
import com.stream.four.repository.SubscriptionRepository;
import com.stream.four.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReferralService {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

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

        var inviterSub = subscriptionRepository.findByUser_UserIdAndStatus(inviter.getUserId(), SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Inviter has no active subscription"));

        inviteeSub.setReferralDiscountApplied(true);
        inviteeSub.setReferralDiscountUsed(true);
        inviterSub.setReferralDiscountApplied(true);
        inviterSub.setReferralDiscountUsed(true);
        invitee.setReferralDiscountUsed(true);
        inviter.setReferralDiscountUsed(true);

        subscriptionRepository.save(inviteeSub);
        subscriptionRepository.save(inviterSub);
        userRepository.save(invitee);
        userRepository.save(inviter);

        return new ReferralDiscountResponse(inviter.getUserId(), invitee.getUserId(), true, true,
                "Referral discount applied to both users.");
    }
}

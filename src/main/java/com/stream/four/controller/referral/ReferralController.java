package com.stream.four.controller.referral;

import com.stream.four.dto.response.referral.ReferralDiscountResponse;
import com.stream.four.model.enums.SubscriptionStatus;
import com.stream.four.model.subscription.Subscription;
import com.stream.four.model.user.User;
import com.stream.four.repository.SubscriptionRepository;
import com.stream.four.repository.UserRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/referral")
@RequiredArgsConstructor
@Tag(name = "Referral Management", description = "Manage invite discounts")
@SecurityRequirement(name = "bearerAuth")
public class ReferralController
{
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    @PostMapping("/apply-discount/{inviteeId}")
    public ResponseEntity<ReferralDiscountResponse> applyReferralDiscount(@PathVariable String inviteeId)
    {
        User invitee = userRepository.findById(inviteeId)
                .orElseThrow(() -> new RuntimeException("Invitee not found"));

        if(invitee.getInvitedBy() == null)
        {
            return ResponseEntity.badRequest().body(new ReferralDiscountResponse(
                    null,
                    invitee.getUserId(),
                    false,
                    false,
                    "Invitee was not invited by anyone."));
        }

        User inviter = userRepository.findById(invitee.getInvitedBy())
                .orElseThrow(() -> new RuntimeException("Inviter not found."));

        if (invitee.isReferralDiscountUsed() || inviter.isReferralDiscountUsed())
        {
            return ResponseEntity.badRequest().body(new ReferralDiscountResponse(
                    inviter.getUserId(),
                    invitee.getUserId(),
                    false,
                    false,
                    "Discount is already used by one of the accounts"));
        }

        Subscription inviteeSub = subscriptionRepository.findByUser_UserIdAndStatus(inviteeId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Invitee subscription not found."));

        Subscription inviterSub = subscriptionRepository.findByUser_UserIdAndStatus(inviter.getUserId(), SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Inviter subscription not found"));

        //Apply discount
        inviteeSub.setReferralDiscountApplied(true);
        inviterSub.setReferralDiscountApplied(true);

        inviteeSub.setReferralDiscountUsed(true);
        inviterSub.setReferralDiscountUsed(true);

        //Mark accounts for using the discount
        invitee.setReferralDiscountUsed(true);
        inviter.setReferralDiscountUsed(true);

        subscriptionRepository.save(inviteeSub);
        subscriptionRepository.save(inviterSub);
        userRepository.save(invitee);
        userRepository.save(inviter);

        return ResponseEntity.ok(new ReferralDiscountResponse(
                inviter.getUserId(),
                invitee.getUserId(),
                true,
                true,
                "Referral discount applied to both users."
        ));

    }
}

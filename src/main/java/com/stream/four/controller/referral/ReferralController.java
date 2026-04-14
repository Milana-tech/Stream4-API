package com.stream.four.controller.referral;

import com.stream.four.dto.response.referral.ReferralDiscountResponse;
import com.stream.four.service.ReferralService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/referrals")
@RequiredArgsConstructor
@Tag(name = "referral-management", description = "Manage invite discounts (supports JSON, XML, CSV)")
@SecurityRequirement(name = "bearerAuth")
public class ReferralController {

    private final ReferralService referralService;

    @PostMapping(value = "/apply-discount/{inviteeId}", produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation(summary = "Apply referral discount", description = "Apply referral discount to both inviter and invitee. Supports JSON, XML, CSV.")
    public ResponseEntity<ReferralDiscountResponse> applyReferralDiscount(@PathVariable String inviteeId) {
        return ResponseEntity.ok(referralService.applyReferralDiscount(inviteeId));
    }
}

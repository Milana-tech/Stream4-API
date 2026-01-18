package com.stream.four.dto.response.referral;

import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlRootElement(name = "ReferralDiscountResponse")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReferralDiscountResponse
{
    private String inviterId;
    private String inviteeId;
    private boolean inviterDiscountApplied;
    private boolean inviteeDiscountApplied;
    private String message;
}

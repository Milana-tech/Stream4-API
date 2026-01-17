package com.stream.four.dto.response.subscription;

import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlRootElement(name = "SubscriptionOverviewResponse")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionOverviewResponse
{
    private String subscriptionSummary;
    private String paymentHistory;
}

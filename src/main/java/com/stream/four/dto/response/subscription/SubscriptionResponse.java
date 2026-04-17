package com.stream.four.dto.response.subscription;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement (localName = "Subscription")
public class SubscriptionResponse
{
    @JacksonXmlProperty(localName = "subscriptionId")
    private Long subscriptionId;

    @JacksonXmlProperty(localName = "userId")
    private String userId;

   @JacksonXmlProperty(localName = "plan")
    private String plan;

    @JacksonXmlProperty(localName = "status")
    private String status;

    @JacksonXmlProperty(localName = "totalPrice")
    private BigDecimal totalPrice;

    @JacksonXmlProperty(localName = "discountPercentage")
    private BigDecimal discountPercentage;

    @JacksonXmlProperty(localName = "discountedPrice")
    private BigDecimal discountedPrice;

    @JacksonXmlProperty(localName = "discountEndDate")
    private LocalDate discountEndDate;

    @JacksonXmlProperty(localName = "startDate")
    private LocalDate startDate;

    @JacksonXmlProperty(localName = "endDate")
    private LocalDate endDate;

    @JacksonXmlProperty(localName = "autoRenew")
    private Boolean autoRenew;

    @JacksonXmlProperty(localName = "daysRemaining")
    private Integer daysRemaining;
}
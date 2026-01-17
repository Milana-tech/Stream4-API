package com.stream.four.dto;

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
public class SubscriptionResponse {

    private Long subscriptionId;
    private String userId;  // ← Changed
    private String packageName;
    private String quality;
    private String status;
    private BigDecimal totalPrice;
    private BigDecimal discountPercentage;
    private BigDecimal discountedPrice;
    private LocalDate discountEndDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean autoRenew;
    private Integer daysRemaining;
}
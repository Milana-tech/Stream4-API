package com.stream.four.model.enums;

import java.math.BigDecimal;

public enum SubscriptionPlan {
    SD(new BigDecimal("7.99")),
    HD(new BigDecimal("12.99")),
    UHD(new BigDecimal("17.99"));

    private final BigDecimal monthlyPrice;

    SubscriptionPlan(BigDecimal monthlyPrice) {
        this.monthlyPrice = monthlyPrice;
    }

    public BigDecimal getMonthlyPrice() {
        return monthlyPrice;
    }
}

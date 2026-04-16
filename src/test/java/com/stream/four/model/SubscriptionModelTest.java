package com.stream.four.model;

import com.stream.four.model.enums.SubscriptionPlan;
import com.stream.four.model.enums.SubscriptionStatus;
import com.stream.four.model.subscription.Subscription;
import com.stream.four.model.subscription.Trial;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionModelTest {

    @Test
    void subscription_isActive_whenStatusActive() {
        var sub = Subscription.builder()
                .status(SubscriptionStatus.ACTIVE)
                .plan(SubscriptionPlan.HD)
                .totalPrice(java.math.BigDecimal.TEN)
                .discountPercentage(java.math.BigDecimal.ZERO)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(1))
                .build();
        assertTrue(sub.isActive());
    }

    @Test
    void subscription_isNotActive_whenCancelled() {
        var sub = Subscription.builder()
                .status(SubscriptionStatus.CANCELLED)
                .plan(SubscriptionPlan.HD)
                .totalPrice(java.math.BigDecimal.TEN)
                .discountPercentage(java.math.BigDecimal.ZERO)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(1))
                .build();
        assertFalse(sub.isActive());
    }

    @Test
    void subscription_prePersist_setsDefaultDates() {
        var sub = new Subscription();
        sub.prePersist();
        assertNotNull(sub.getStartDate());
        assertNotNull(sub.getEndDate());
        assertEquals(sub.getStartDate().plusMonths(1), sub.getEndDate());
    }

    @Test
    void subscription_prePersist_doesNotOverwriteExistingDates() {
        var sub = new Subscription();
        var start = LocalDate.of(2025, 1, 1);
        var end = LocalDate.of(2025, 6, 1);
        sub.setStartDate(start);
        sub.setEndDate(end);
        sub.prePersist();
        assertEquals(start, sub.getStartDate());
        assertEquals(end, sub.getEndDate());
    }

    @Test
    void trial_prePersist_setsDefaultDates() {
        var trial = new Trial();
        trial.prePersist();
        assertNotNull(trial.getStartDate());
        assertNotNull(trial.getEndDate());
        assertEquals(trial.getStartDate().plusDays(7), trial.getEndDate());
    }
}

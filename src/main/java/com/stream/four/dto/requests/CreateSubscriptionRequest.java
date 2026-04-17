package com.stream.four.dto.requests;

import com.stream.four.model.enums.SubscriptionPlan;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateSubscriptionRequest {

    @NotNull(message = "Plan is required")
    private SubscriptionPlan plan;
}

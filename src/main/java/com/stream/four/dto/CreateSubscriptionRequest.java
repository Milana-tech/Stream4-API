package com.stream.four.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateSubscriptionRequest {

    @NotBlank
    @Size(max = 50)
    private String plan;
}

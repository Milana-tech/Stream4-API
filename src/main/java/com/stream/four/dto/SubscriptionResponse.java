package com.stream.four.dto;

import lombok.Data;

@Data
public class SubscriptionResponse {
    private String id;

    private String plan;
    
    private long startDate;

    private long endDate;
    
    private boolean active;
}

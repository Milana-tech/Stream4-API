package com.stream.four.dto;

import lombok.Data;

@Data
public class TrialResponse {
    private String id;

    private long startDate;

    private long endDate;
    
    private boolean used;
}

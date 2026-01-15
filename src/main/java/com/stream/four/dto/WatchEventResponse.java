package com.stream.four.dto;

import lombok.Data;

@Data
public class WatchEventResponse {
    private String titleId;

    private int progressSeconds;
    
    private boolean finished;
    
    private long lastUpdated;
}

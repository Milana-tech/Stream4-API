package com.stream.four.dto;

import lombok.Data;

@Data
public class CreateWatchEventRequest {
    private String titleId;

    private int progressSeconds;
    
    private boolean finished;
}

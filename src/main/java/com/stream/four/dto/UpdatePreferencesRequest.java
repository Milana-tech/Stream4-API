package com.stream.four.dto;

import lombok.Data;

@Data
public class UpdatePreferencesRequest {
    private String language;

    private String maturityLevel;
    
    private String genres;
}

package com.stream.four.dto;

import lombok.Data;

@Data
public class PreferencesResponse {
    private String language;

    private String maturityLevel;
    
    private String genres;
}

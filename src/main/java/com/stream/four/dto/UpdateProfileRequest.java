package com.stream.four.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    
    private String name;
    private String avatar;
    private String maturityLevel;
}

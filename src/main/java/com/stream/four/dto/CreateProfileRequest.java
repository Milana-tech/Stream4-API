package com.stream.four.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateProfileRequest {
    
    @NotBlank
    private String name;
    private String avatar;
    private String maturityLevel;
}

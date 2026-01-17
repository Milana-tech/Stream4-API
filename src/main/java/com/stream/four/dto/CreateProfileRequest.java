package com.stream.four.dto;

import com.stream.four.validation.ValidMaturityLevel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateProfileRequest {
    
    @NotBlank(message = "Profile name is required")
    private String name;

    private String avatar;

    @ValidMaturityLevel
    @NotNull(message = "Maturity level is required")
    private String maturityLevel;
}

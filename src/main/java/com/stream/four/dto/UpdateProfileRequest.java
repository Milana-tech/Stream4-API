package com.stream.four.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    
    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String avatar;

    @Size(max = 50)
    private String maturityLevel;
}

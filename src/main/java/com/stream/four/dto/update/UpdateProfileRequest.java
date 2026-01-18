package com.stream.four.dto.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    
    @Size(max = 100)
    @NotBlank(message = "Profile name is required")
    private String name;

    @Size(max = 500)
    private String avatar;

    @Size(max = 50)
    @NotNull(message = "Maturity level is required")
    private String maturityLevel;
}

package com.stream.four.dto.update;

import com.stream.four.validation.ValidMaturityLevel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePreferencesRequest {

    @Size(max = 20)
    @NotBlank(message = "Language is required")
    private String language;

    @Size(max = 50)
    @ValidMaturityLevel
    private String maturityLevel;

    @Size(max = 500)
    @NotBlank(message = "Genres cannot be empty")
    private String genres;
}

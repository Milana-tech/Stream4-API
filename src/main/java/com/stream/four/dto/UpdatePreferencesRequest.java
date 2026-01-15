package com.stream.four.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePreferencesRequest {

    @Size(max = 20)
    private String language;

    @Size(max = 50)
    private String maturityLevel;

    @Size(max = 500)
    private String genres;
}

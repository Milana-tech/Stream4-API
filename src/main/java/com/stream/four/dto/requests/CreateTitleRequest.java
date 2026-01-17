package com.stream.four.dto.requests;

import com.stream.four.model.enums.TitleType;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTitleRequest {
    
    @NotBlank
    private String name;

    private String description;

    private int releaseYear;

    private TitleType type;

    private String genre;
}

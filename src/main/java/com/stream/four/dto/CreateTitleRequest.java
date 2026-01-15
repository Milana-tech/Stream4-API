package com.stream.four.dto;

import com.stream.four.model.TitleType;

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

package com.stream.four.dto.requests;

import com.stream.four.model.enums.ContentWarning;
import com.stream.four.model.enums.MaturityRating;
import com.stream.four.model.enums.TitleType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
public class CreateTitleRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Release year is required")
    @Min(value = 1888, message = "Release year must be 1888 or later")
    private Integer releaseYear;

    @NotNull(message = "Type is required")
    private TitleType type;

    private String genre;

    private MaturityRating maturityRating;

    private Set<ContentWarning> contentWarnings;
}

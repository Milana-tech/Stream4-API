package com.stream.four.dto.requests;

import com.stream.four.model.enums.ContentWarning;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateProfileRequest {

    @NotBlank(message = "Profile name is required")
    private String name;

    private String avatar;

    @NotNull(message = "Age is required")
    @Min(value = 3, message = "Age must be 3 or greater")
    @Max(value = 95, message = "Age must be 95 or less")
    private Integer age;

    private List<ContentWarning> contentFilters;
}

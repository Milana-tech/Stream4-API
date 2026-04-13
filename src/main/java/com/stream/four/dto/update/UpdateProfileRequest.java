package com.stream.four.dto.update;

import com.stream.four.model.enums.ContentWarning;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UpdateProfileRequest {

    @Size(max = 100)
    @NotBlank(message = "Profile name is required")
    private String name;

    @Size(max = 500)
    private String avatar;

    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age must be 0 or greater")
    private Integer age;

    private List<ContentWarning> contentFilters;
}

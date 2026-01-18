package com.stream.four.dto.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateWatchEventRequest {

    @NotBlank(message = "TitleId is required")
    @Size(max = 64)
    private String titleId;

    @NotBlank(message = "ProfileId is required")
    private String profileId;

    @Min(value = 0, message = "Progress seconds cannot be negative")
    private int progressSeconds;

    @NotNull(message = "Finished flag is required")
    private Boolean finished;
}

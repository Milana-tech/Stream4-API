package com.stream.four.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateWatchEventRequest {

    @NotBlank
    @Size(max = 64)
    private String titleId;

    @Min(0)
    private int progressSeconds;

    private boolean finished;
}

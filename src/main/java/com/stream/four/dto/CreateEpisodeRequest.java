package com.stream.four.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateEpisodeRequest {

    @Min(1)
    @Max(10_000)
    private int episodeNumber;

    @NotBlank
    @Size(max = 200)
    private String title;

    @Size(max = 2000)
    private String description;
}

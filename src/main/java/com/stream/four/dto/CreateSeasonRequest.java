package com.stream.four.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class CreateSeasonRequest {

    @Min(1)
    @Max(10_000)
    private int seasonNumber;
}

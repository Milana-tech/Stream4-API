package com.stream.four.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateWatchlistItemRequest {

    @NotBlank
    @Size(max = 64)
    private String titleId;
}

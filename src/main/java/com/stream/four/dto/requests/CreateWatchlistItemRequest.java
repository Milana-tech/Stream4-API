package com.stream.four.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateWatchlistItemRequest {

    @NotBlank(message = "TitleId is required")
    @Size(max = 64)
    private String titleId;

    @NotBlank(message = "ProfileId is required")
    private String profileId;
}

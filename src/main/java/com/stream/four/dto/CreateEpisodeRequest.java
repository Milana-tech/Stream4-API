package com.stream.four.dto;

import lombok.Data;

@Data
public class CreateEpisodeRequest {
    private int episodeNumber;

    private String name;
    
    private String description;
}

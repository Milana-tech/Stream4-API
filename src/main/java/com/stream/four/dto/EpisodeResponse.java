package com.stream.four.dto;

import lombok.Data;

@Data
public class EpisodeResponse {
    private String id;

    private int episodeNumber;

    private String name;

    private String description;
}

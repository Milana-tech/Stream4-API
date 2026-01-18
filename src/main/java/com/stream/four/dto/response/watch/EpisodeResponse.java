package com.stream.four.dto.response.watch;

import lombok.Data;

@Data
public class EpisodeResponse {
    private String id;

    private int episodeNumber;

    private String name;

    private String description;
}

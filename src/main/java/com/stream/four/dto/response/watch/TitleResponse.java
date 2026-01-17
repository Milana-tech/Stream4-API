package com.stream.four.dto.response.watch;

import com.stream.four.model.enums.TitleType;

import lombok.Data;

@Data
public class TitleResponse {
    private String id;

    private String name;

    private String description;

    private int releaseYear;

    private TitleType type;
    
    private String genre;
}

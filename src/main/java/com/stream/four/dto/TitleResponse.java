package com.stream.four.dto;

import com.stream.four.model.TitleType;

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

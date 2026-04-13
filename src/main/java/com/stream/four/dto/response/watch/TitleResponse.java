package com.stream.four.dto.response.watch;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.stream.four.model.enums.ContentWarning;
import com.stream.four.model.enums.MaturityRating;
import com.stream.four.model.enums.TitleType;
import lombok.Data;

import java.util.Set;

@Data
@JacksonXmlRootElement(localName = "Title")
public class TitleResponse {

    @JacksonXmlProperty(localName = "id")
    private String id;

    @JacksonXmlProperty(localName = "name")
    private String name;

    @JacksonXmlProperty(localName = "description")
    private String description;

    @JacksonXmlProperty(localName = "releaseYear")
    private int releaseYear;

    @JacksonXmlProperty(localName = "type")
    private TitleType type;

    @JacksonXmlProperty(localName = "genre")
    private String genre;

    @JacksonXmlProperty(localName = "maturityRating")
    private MaturityRating maturityRating;

    @JacksonXmlProperty(localName = "contentWarnings")
    private Set<ContentWarning> contentWarnings;
}

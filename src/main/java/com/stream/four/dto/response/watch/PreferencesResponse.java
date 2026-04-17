package com.stream.four.dto.response.watch;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.stream.four.model.enums.Genre;
import com.stream.four.model.enums.MaturityRating;
import com.stream.four.model.enums.TitleType;
import lombok.Data;

import java.util.Set;

@Data
@JacksonXmlRootElement(localName = "preferences")
public class PreferencesResponse {

    @JacksonXmlProperty(localName = "profileId")
    private String profileId;

    @JacksonXmlProperty(localName = "preferredGenres")
    private Set<Genre> preferredGenres;

    @JacksonXmlProperty(localName = "preferredType")
    private TitleType preferredType;

    @JacksonXmlProperty(localName = "minimumMaturityRating")
    private MaturityRating minimumMaturityRating;
}

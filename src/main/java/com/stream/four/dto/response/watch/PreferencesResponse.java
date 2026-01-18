package com.stream.four.dto.response.watch;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "Preferences")
public class PreferencesResponse {
    @JacksonXmlProperty(localName = "language")
    private String language;

    @JacksonXmlProperty(localName = "maturityLevel")
    private String maturityLevel;

    @JacksonXmlProperty(localName = "genres")
    private String genres;
}

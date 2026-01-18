package com.stream.four.dto.response.watch;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement (localName = "Season")
public class SeasonResponse
{
    @JacksonXmlProperty (localName = "id")
    private String id;

    @JacksonXmlProperty (localName = "seasonNumber")
    private int seasonNumber;
}

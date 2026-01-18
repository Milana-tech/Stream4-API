package com.stream.four.dto.response.watch;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement (localName = "Episode")
public class EpisodeResponse
{
    @JacksonXmlProperty (localName = "id")
    private String id;

    @JacksonXmlProperty (localName = "episodeNumber")
    private int episodeNumber;

    @JacksonXmlProperty (localName = "name")
    private String name;

    @JacksonXmlProperty (localName = "description")
    private String description;
}

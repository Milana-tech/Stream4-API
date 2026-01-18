package com.stream.four.dto.response.watch;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement (localName = "WatchEvent")
public class WatchEventResponse
{
    @JacksonXmlProperty (localName = "titleId")
    private String titleId;

    @JacksonXmlProperty (localName = "progressSeconds")
    private int progressSeconds;

    @JacksonXmlProperty (localName = "finished")
    private boolean finished;

    @JacksonXmlProperty (localName = "lastUpdated")
    private long lastUpdated;
}

package com.stream.four.dto.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName = "Invitation")
public class InvitationResponse {

    @JacksonXmlProperty(localName = "invitationLink")
    private String invitationLink;

    @JacksonXmlProperty(localName = "inviteeEmail")
    private String inviteeEmail;
}

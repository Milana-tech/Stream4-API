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

    @JacksonXmlProperty(localName = "id")
    private String id;

    @JacksonXmlProperty(localName = "invitationLink")
    private String invitationLink;

    @JacksonXmlProperty(localName = "inviteeEmail")
    private String inviteeEmail;

    @JacksonXmlProperty(localName = "inviteeUserId")
    private String inviteeUserId;

    @JacksonXmlProperty(localName = "used")
    private boolean used;

    @JacksonXmlProperty(localName = "discountApplied")
    private boolean discountApplied;

    @JacksonXmlProperty(localName = "discountAppliedAt")
    private java.time.LocalDate discountAppliedAt;

    @JacksonXmlProperty(localName = "discountEndDate")
    private java.time.LocalDate discountEndDate;
}

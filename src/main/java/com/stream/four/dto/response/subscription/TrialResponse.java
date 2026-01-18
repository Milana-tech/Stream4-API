package com.stream.four.dto.response.subscription;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement (localName = "Trial")
public class TrialResponse
{
    @JacksonXmlProperty (localName = "trialId")
    private Long trialId;

    @JacksonXmlProperty (localName = "userId")
    private String userId;

    @JacksonXmlProperty (localName = "startDate")
    private LocalDate startDate;

    @JacksonXmlProperty (localName = "endDate")
    private LocalDate endDate;

    @JacksonXmlProperty (localName = "status")
    private String status;

    @JacksonXmlProperty (localName = "daysRemaining")
    private Integer daysRemaining;

    @JacksonXmlProperty (localName = "convertedToPaid")
    private Boolean convertedToPaid;

    @JacksonXmlProperty (localName = "convertedDate")
    private LocalDate convertedDate;
}
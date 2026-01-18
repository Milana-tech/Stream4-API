package com.stream.four.dto.response.user;

import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlRootElement(name = "ProfileStatusResponse")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileStatusResponse
{
    private String profileId;
    private String status;
}

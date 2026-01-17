package com.stream.four.dto.response.user;

import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlRootElement(name = "EmployeeBasicInfo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeBasicInfoResponse
{
    private String userId;
    private String name;
    private String email;
    private String role;
}

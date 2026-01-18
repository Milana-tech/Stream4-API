package com.stream.four.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateInvitationRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String inviteeEmail;
}

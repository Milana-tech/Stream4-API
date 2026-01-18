package com.stream.four.dto.requests;

import com.stream.four.validation.ValidUUID;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AcceptInvitationRequest {

    @NotBlank(message = "Invitation token is required")
    @ValidUUID
    private String token;

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}

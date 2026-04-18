package com.stream.four.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {

    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @Email(message = "Email should be valid")
    private String email;
}

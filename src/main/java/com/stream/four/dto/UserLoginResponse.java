package com.stream.four.dto;

import com.stream.four.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginResponse
{
    private String id;
    private String name;
    private String email;
    private Role role;
    private String token;
}
package com.stream.four.controller;

import com.stream.four.dto.UserResponse;
import com.stream.four.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@Tag(name = "Users", description = "Endpoints to manage users")
public class UserController
{
    private final UserService userService;

    @GetMapping("/api/users")
    @Operation(
            summary = "Get all users",
            description = "Retrieve a list of all users in the home automation system"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @PreAuthorize("hasRole('ADMINISTRATOR') or #userId == authentication.principal.username")
    @GetMapping("/api/users/{userId}")
    @Operation(
            summary = "Get user by id",
            description = "Retrieve a user from the home automation system by id"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved specific user")
    public UserResponse getUser(@PathVariable String userId) {
        return userService.getUser(userId);
    }
}

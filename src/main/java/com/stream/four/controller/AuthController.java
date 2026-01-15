package com.stream.four.controller;

import com.stream.four.dto.CreateUserRequest;
import com.stream.four.dto.LoginRequest;
import com.stream.four.dto.UserLoginResponse;
import com.stream.four.mapper.InvitationHelper;
import com.stream.four.service.LoginService;
import com.stream.four.service.UserService;
import com.stream.four.service.auth.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Login", description = "Endpoints for login in login out")
public class AuthController {

    private final LoginService loginService;
    private final JwtService jwtService;
    private final UserService userService;
    private final InvitationHelper invitationHelper;

    @PostMapping("/login")
    @Operation(
            summary = "Login as a user",
            description = "Logins a user into the system"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users")
    public UserLoginResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        var user = loginService.login(loginRequest);
        if (user.isAccountLocked()) 
        {
            throw new RuntimeException("Account is locked due to too many failed login attempts.");
        }
        
        var token = jwtService.generateToken(user.getId(), user.getRole().name());
        return new UserLoginResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), token);
    }

    @PostMapping("/register")
    @Operation(
            summary = "Register as a user",
            description = "Register a user in the home automation system"
    )
    @ApiResponse(responseCode = "200", description = "Successfully created a user")
    public UserLoginResponse register(@Valid @RequestBody CreateUserRequest createUserRequest) {
        // handle invitation token if present
        if (createUserRequest.getInvitationToken() != null)
        {
            // call repository or helper to validate + link accounts
            invitationHelper.validatedToken(createUserRequest.getInvitationToken());
            invitationHelper.linkAccounts(createUserRequest.getInvitationToken(), createUserRequest.getEmail());
        }
        var user = userService.createUser(createUserRequest);
        var token = jwtService.generateToken(user.getId(), user.getRole().name());
        return new UserLoginResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), token);
    }
}
package com.stream.four.controller;

import com.stream.four.dto.requests.CreateUserRequest;
import com.stream.four.dto.response.MessageResponse;
import com.stream.four.dto.response.user.LoginRequest;
import com.stream.four.dto.response.user.UserLoginResponse;
import com.stream.four.exception.ErrorResponse;
import com.stream.four.mapper.InvitationHelper;
import com.stream.four.service.LoginService;
import com.stream.four.service.UserService;
import com.stream.four.service.auth.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "auth", description = "Endpoints for login and logout")
public class AuthController {

    private final LoginService loginService;
    private final JwtService jwtService;
    private final UserService userService;
    private final InvitationHelper invitationHelper;

    @PostMapping(value = "/login", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(summary = "Login as a user", description = "Logins a user into the system")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully logged in"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserLoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        var user = loginService.login(loginRequest);
        var token = jwtService.generateToken(user.getUserId(), user.getRole().name());
        return ResponseEntity.ok(new UserLoginResponse(user.getUserId(), user.getName(), user.getEmail(), user.getRole(), token));
    }

    @GetMapping(value = "/verify", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(summary = "Verify account", description = "Activate account using the token sent by email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account successfully verified"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired verification token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MessageResponse> verify(@RequestParam String token) {
        userService.verifyAccount(token);
        return ResponseEntity.ok(new MessageResponse("Account verified successfully. You can now log in."));
    }

    @PostMapping(value = "/register", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(summary = "Register as a user", description = "Register a user in the system")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successfully created a user"),
            @ApiResponse(responseCode = "400", description = "Invalid input - validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Email already in use",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserLoginResponse> register(@Valid @RequestBody CreateUserRequest createUserRequest) {
        if (createUserRequest.getInvitationToken() != null) {
            invitationHelper.validatedToken(createUserRequest.getInvitationToken());
            invitationHelper.linkAccounts(createUserRequest.getInvitationToken(), createUserRequest.getEmail());
        }
        var user = userService.createUser(createUserRequest);
        var token = jwtService.generateToken(user.getId(), user.getRole().name());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new UserLoginResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), token));
    }
}

package com.stream.four.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stream.four.dto.response.user.LoginRequest;
import com.stream.four.dto.response.user.UserLoginResponse;
import com.stream.four.dto.response.user.UserResponse;
import com.stream.four.exception.GlobalExceptionHandler;
import com.stream.four.mapper.InvitationHelper;
import com.stream.four.model.enums.Role;
import com.stream.four.model.user.User;
import com.stream.four.service.LoginService;
import com.stream.four.service.UserService;
import com.stream.four.service.auth.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final LoginService loginService = mock(LoginService.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final UserService userService = mock(UserService.class);
    private final InvitationHelper invitationHelper = mock(InvitationHelper.class);

    @BeforeEach
    void setUp() {
        var controller = new AuthController(loginService, jwtService, userService, invitationHelper);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void login_validCredentials_returns200() throws Exception {
        var user = new User();
        user.setUserId("u1");
        user.setName("Alice");
        user.setEmail("alice@example.com");
        user.setRole(Role.USER);

        when(loginService.login(any())).thenReturn(user);
        when(jwtService.generateToken("u1", "USER")).thenReturn("jwt-token");

        var body = new LoginRequest("alice@example.com", "password123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void login_missingPassword_returns400() throws Exception {
        var body = new LoginRequest("alice@example.com", "");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_serviceThrows_returns400() throws Exception {
        when(loginService.login(any())).thenThrow(new IllegalArgumentException("Invalid credentials."));

        var body = new LoginRequest("alice@example.com", "wrongpass");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void verify_validToken_returns200() throws Exception {
        doNothing().when(userService).verifyAccount("tok123");

        mockMvc.perform(get("/auth/verify").param("token", "tok123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Account verified successfully. You can now log in."));
    }

    @Test
    void verify_invalidToken_returns500() throws Exception {
        doThrow(new RuntimeException("Invalid token")).when(userService).verifyAccount("bad");

        mockMvc.perform(get("/auth/verify").param("token", "bad"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void register_validRequest_returns201() throws Exception {
        var dto = new UserResponse("u2", "Bob", "bob@example.com", Role.USER, false);
        when(userService.createUser(any())).thenReturn(dto);
        when(jwtService.generateToken("u2", "USER")).thenReturn("new-token");

        var body = """
                {
                  "name": "Bob",
                  "email": "bob@example.com",
                  "password": "password123",
                  "role": "USER"
                }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("new-token"));
    }

    @Test
    void register_missingEmail_returns400() throws Exception {
        var body = """
                {
                  "name": "Bob",
                  "password": "password123",
                  "role": "USER"
                }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}

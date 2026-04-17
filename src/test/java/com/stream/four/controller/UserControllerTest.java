package com.stream.four.controller;

import com.stream.four.controller.user.UserController;
import com.stream.four.dto.response.user.UserResponse;
import com.stream.four.exception.GlobalExceptionHandler;
import com.stream.four.exception.ResourceNotFoundException;
import com.stream.four.model.enums.Role;
import com.stream.four.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    private MockMvc mockMvc;
    private final UserService userService = mock(UserService.class);

    @BeforeEach
    void setUp() {
        var controller = new UserController(userService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAllUsers_returnsList() throws Exception {
        var u = new UserResponse("u1", "Alice", "alice@example.com", Role.USER, false);
        when(userService.getAllUsers()).thenReturn(List.of(u));

        mockMvc.perform(get("/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("alice@example.com"));
    }

    @Test
    void getUser_found_returns200() throws Exception {
        var u = new UserResponse("u1", "Alice", "alice@example.com", Role.USER, false);
        when(userService.getUser("u1")).thenReturn(u);

        mockMvc.perform(get("/users/u1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("u1"));
    }

    @Test
    void getUser_notFound_returns404() throws Exception {
        when(userService.getUser("missing"))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/users/missing").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

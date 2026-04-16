package com.stream.four.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stream.four.controller.user.ProfileController;
import com.stream.four.dto.response.user.ProfileResponse;
import com.stream.four.exception.GlobalExceptionHandler;
import com.stream.four.exception.ResourceNotFoundException;
import com.stream.four.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProfileControllerTest {

    private MockMvc mockMvc;
    private final ProfileService profileService = mock(ProfileService.class);

    @BeforeEach
    void setUp() {
        var controller = new ProfileController(profileService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private ProfileResponse sample() {
        var p = new ProfileResponse();
        p.setId("p1");
        p.setName("Alice Main");
        p.setAge(30);
        return p;
    }

    private UsernamePasswordAuthenticationToken principal() {
        return new UsernamePasswordAuthenticationToken("user-001", null, List.of());
    }

    @Test
    void getProfiles_returnsList() throws Exception {
        when(profileService.getProfiles("user-001")).thenReturn(List.of(sample()));

        mockMvc.perform(get("/profiles")
                        .principal(principal())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Alice Main"));
    }

    @Test
    void getProfile_found_returns200() throws Exception {
        when(profileService.getProfile("p1")).thenReturn(sample());

        mockMvc.perform(get("/profiles/p1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("p1"));
    }

    @Test
    void getProfile_notFound_returns404() throws Exception {
        when(profileService.getProfile("missing"))
                .thenThrow(new ResourceNotFoundException("Profile not found"));

        mockMvc.perform(get("/profiles/missing").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProfile_valid_returns201() throws Exception {
        when(profileService.createProfile(eq("user-001"), any())).thenReturn(sample());

        var body = """
                { "name": "Alice Main", "age": 30 }
                """;

        mockMvc.perform(post("/profiles")
                        .principal(principal())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Alice Main"));
    }

    @Test
    void createProfile_missingAge_returns400() throws Exception {
        var body = """
                { "name": "Alice Main" }
                """;

        mockMvc.perform(post("/profiles")
                        .principal(principal())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProfile_valid_returns200() throws Exception {
        when(profileService.updateProfile(eq("p1"), any())).thenReturn(sample());

        var body = """
                { "name": "Updated", "age": 25 }
                """;

        mockMvc.perform(put("/profiles/p1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void deleteProfile_returns204() throws Exception {
        doNothing().when(profileService).deleteProfile("p1");

        mockMvc.perform(delete("/profiles/p1"))
                .andExpect(status().isNoContent());
    }
}

package com.stream.four.controller;

import com.stream.four.controller.watch.PreferencesController;
import com.stream.four.dto.response.watch.PreferencesResponse;
import com.stream.four.exception.GlobalExceptionHandler;
import com.stream.four.service.PreferencesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PreferencesControllerTest {

    private MockMvc mockMvc;
    private final PreferencesService preferencesService = mock(PreferencesService.class);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new PreferencesController(preferencesService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getPreferences_returns200() throws Exception {
        when(preferencesService.getPreferences("p1")).thenReturn(new PreferencesResponse());

        mockMvc.perform(get("/profiles/p1/preferences").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void updatePreferences_returns200() throws Exception {
        when(preferencesService.updatePreferences(eq("p1"), any())).thenReturn(new PreferencesResponse());

        mockMvc.perform(put("/profiles/p1/preferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }
}

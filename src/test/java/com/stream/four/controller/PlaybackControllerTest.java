package com.stream.four.controller;

import com.stream.four.controller.watch.PlaybackController;
import com.stream.four.exception.GlobalExceptionHandler;
import com.stream.four.exception.ResourceNotFoundException;
import com.stream.four.service.PlaybackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PlaybackControllerTest {

    private MockMvc mockMvc;
    private final PlaybackService playbackService = mock(PlaybackService.class);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new PlaybackController(playbackService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testPlayback_returns200() throws Exception {
        when(playbackService.getPlaybackQuality("alice@example.com", "Breaking Bad", "p1"))
                .thenReturn("User alice@example.com is watching Breaking Bad in HD");

        mockMvc.perform(get("/playback/test-playback")
                        .param("email", "alice@example.com")
                        .param("titleName", "Breaking Bad")
                        .param("profileId", "p1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User alice@example.com is watching Breaking Bad in HD"));
    }

    @Test
    void testPlayback_userNotFound_returns404() throws Exception {
        when(playbackService.getPlaybackQuality("missing@example.com", "Breaking Bad", "p1"))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/playback/test-playback")
                        .param("email", "missing@example.com")
                        .param("titleName", "Breaking Bad")
                        .param("profileId", "p1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testPlayback_missingParam_returns400() throws Exception {
        mockMvc.perform(get("/playback/test-playback")
                        .param("email", "alice@example.com")
                        .param("titleName", "Breaking Bad")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}

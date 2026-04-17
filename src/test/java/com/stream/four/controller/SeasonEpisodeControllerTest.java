package com.stream.four.controller;

import com.stream.four.controller.watch.EpisodeController;
import com.stream.four.controller.watch.SeasonController;
import com.stream.four.dto.response.watch.EpisodeResponse;
import com.stream.four.dto.response.watch.SeasonResponse;
import com.stream.four.exception.GlobalExceptionHandler;
import com.stream.four.service.EpisodeService;
import com.stream.four.service.SeasonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SeasonEpisodeControllerTest {

    private MockMvc seasonMvc;
    private MockMvc episodeMvc;

    private final SeasonService seasonService = mock(SeasonService.class);
    private final EpisodeService episodeService = mock(EpisodeService.class);

    @BeforeEach
    void setUp() {
        seasonMvc = MockMvcBuilders
                .standaloneSetup(new SeasonController(seasonService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        episodeMvc = MockMvcBuilders
                .standaloneSetup(new EpisodeController(episodeService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // --- Season ---

    @Test
    void createSeason_valid_returns201() throws Exception {
        var s = new SeasonResponse();
        s.setId("s1");
        s.setSeasonNumber(1);
        when(seasonService.createSeason(eq("t1"), any())).thenReturn(s);

        seasonMvc.perform(post("/titles/t1/seasons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"seasonNumber\": 1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.seasonNumber").value(1));
    }

    @Test
    void getSeasons_returnsList() throws Exception {
        var s = new SeasonResponse();
        s.setId("s1");
        when(seasonService.getSeasonsForTitle("t1")).thenReturn(List.of(s));

        seasonMvc.perform(get("/titles/t1/seasons").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("s1"));
    }

    // --- Episode ---

    @Test
    void createEpisode_valid_returns201() throws Exception {
        var e = new EpisodeResponse();
        e.setId("ep1");
        e.setEpisodeNumber(1);
        when(episodeService.createEpisode(eq("s1"), any())).thenReturn(e);

        episodeMvc.perform(post("/seasons/s1/episodes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"episodeNumber\": 1, \"durationSeconds\": 2700, \"title\": \"Pilot\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.episodeNumber").value(1));
    }

    @Test
    void getEpisodes_returnsList() throws Exception {
        var e = new EpisodeResponse();
        e.setId("ep1");
        when(episodeService.getEpisodesForSeason("s1")).thenReturn(List.of(e));

        episodeMvc.perform(get("/seasons/s1/episodes").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("ep1"));
    }

    @Test
    void createEpisode_missingTitle_returns400() throws Exception {
        episodeMvc.perform(post("/seasons/s1/episodes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"episodeNumber\": 1, \"durationSeconds\": 2700}"))
                .andExpect(status().isBadRequest());
    }
}

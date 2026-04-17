package com.stream.four.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stream.four.controller.watch.TitleController;
import com.stream.four.dto.response.watch.TitleResponse;
import com.stream.four.exception.GlobalExceptionHandler;
import com.stream.four.exception.ResourceNotFoundException;
import com.stream.four.model.enums.TitleType;
import com.stream.four.service.TitleService;
import com.stream.four.service.TvMazeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TitleControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final TitleService titleService = mock(TitleService.class);
    private final TvMazeService tvMazeService = mock(TvMazeService.class);

    @BeforeEach
    void setUp() {
        var controller = new TitleController(titleService, tvMazeService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private TitleResponse sampleTitle() {
        var t = new TitleResponse();
        t.setId("t1");
        t.setName("Breaking Bad");
        t.setType(TitleType.SERIES);
        return t;
    }

    @Test
    void getAll_returnsList() throws Exception {
        when(titleService.getAllTitles()).thenReturn(List.of(sampleTitle()));

        mockMvc.perform(get("/titles").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Breaking Bad"));
    }

    @Test
    void getById_found_returns200() throws Exception {
        when(titleService.getTitleById("t1")).thenReturn(sampleTitle());

        mockMvc.perform(get("/titles/t1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("t1"));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(titleService.getTitleById("missing"))
                .thenThrow(new ResourceNotFoundException("Title not found"));

        mockMvc.perform(get("/titles/missing").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_validRequest_returns201() throws Exception {
        when(titleService.createTitle(any())).thenReturn(sampleTitle());

        var body = """
                {
                  "name": "Breaking Bad",
                  "releaseYear": 2008,
                  "type": "SERIES",
                  "supportedQualities": ["HD"]
                }
                """;

        mockMvc.perform(post("/titles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Breaking Bad"));
    }

    @Test
    void create_missingName_returns400() throws Exception {
        var body = """
                {
                  "releaseYear": 2008,
                  "type": "SERIES",
                  "supportedQualities": ["HD"]
                }
                """;

        mockMvc.perform(post("/titles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getForProfile_returns200() throws Exception {
        when(titleService.getTitlesForProfile("p1")).thenReturn(List.of(sampleTitle()));

        mockMvc.perform(get("/titles/for-profile/p1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("t1"));
    }

    @Test
    void searchTvMaze_returns200() throws Exception {
        when(tvMazeService.searchShows("breaking")).thenReturn(List.of());

        mockMvc.perform(get("/titles/tvmaze/search").param("query", "breaking")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void lookupTvMaze_returns200() throws Exception {
        when(tvMazeService.getShow("breaking bad")).thenReturn(null);

        mockMvc.perform(get("/titles/tvmaze/lookup").param("query", "breaking bad")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}

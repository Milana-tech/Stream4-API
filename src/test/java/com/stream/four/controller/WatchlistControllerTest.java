package com.stream.four.controller;

import com.stream.four.controller.watch.WatchlistController;
import com.stream.four.dto.response.watch.WatchlistItemResponse;
import com.stream.four.exception.GlobalExceptionHandler;
import com.stream.four.service.WatchlistService;
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

class WatchlistControllerTest {

    private MockMvc mockMvc;
    private final WatchlistService watchlistService = mock(WatchlistService.class);

    @BeforeEach
    void setUp() {
        var controller = new WatchlistController(watchlistService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private UsernamePasswordAuthenticationToken principal() {
        return new UsernamePasswordAuthenticationToken("user-001", null, List.of());
    }

    private WatchlistItemResponse sample() {
        var w = new WatchlistItemResponse();
        w.setId("wl-1");
        w.setTitleId("t1");
        return w;
    }

    @Test
    void getAll_returnsList() throws Exception {
        when(watchlistService.getAll("user-001", "p1")).thenReturn(List.of(sample()));

        mockMvc.perform(get("/watchlists")
                        .param("profileId", "p1")
                        .principal(principal())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titleId").value("t1"));
    }

    @Test
    void add_valid_returns201() throws Exception {
        when(watchlistService.add(eq("user-001"), any())).thenReturn(sample());

        var body = """
                { "titleId": "t1", "profileId": "p1" }
                """;

        mockMvc.perform(post("/watchlists")
                        .principal(principal())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("wl-1"));
    }

    @Test
    void remove_returns204() throws Exception {
        doNothing().when(watchlistService).remove("user-001", "wl-1");

        mockMvc.perform(delete("/watchlists/wl-1").principal(principal()))
                .andExpect(status().isNoContent());
    }
}

package com.stream.four.controller;

import com.stream.four.controller.watch.ViewingBehaviourController;
import com.stream.four.dto.response.watch.WatchEventResponse;
import com.stream.four.exception.GlobalExceptionHandler;
import com.stream.four.exception.ResourceNotFoundException;
import com.stream.four.service.ViewingBehaviourService;
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

class ViewingBehaviourControllerTest {

    private MockMvc mockMvc;
    private final ViewingBehaviourService service = mock(ViewingBehaviourService.class);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ViewingBehaviourController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private UsernamePasswordAuthenticationToken principal() {
        return new UsernamePasswordAuthenticationToken("user-001", null, List.of());
    }

    private WatchEventResponse sample() {
        var w = new WatchEventResponse();
        w.setTitleId("t1");
        return w;
    }

    @Test
    void watch_valid_returns201() throws Exception {
        when(service.watch(eq("user-001"), any())).thenReturn(sample());

        var body = """
                { "titleId": "t1", "profileId": "p1", "progressSeconds": 0, "finished": false }
                """;

        mockMvc.perform(post("/viewing-behaviour/watch")
                        .principal(principal())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titleId").value("t1"));
    }

    @Test
    void history_returnsList() throws Exception {
        when(service.history("user-001")).thenReturn(List.of(sample()));

        mockMvc.perform(get("/viewing-behaviour/history")
                        .principal(principal())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titleId").value("t1"));
    }

    @Test
    void progress_found_returns200() throws Exception {
        when(service.progress("user-001", "t1")).thenReturn(sample());

        mockMvc.perform(get("/viewing-behaviour/progress/t1")
                        .principal(principal())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titleId").value("t1"));
    }

    @Test
    void progress_notFound_returns404() throws Exception {
        when(service.progress("user-001", "missing"))
                .thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/viewing-behaviour/progress/missing")
                        .principal(principal())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

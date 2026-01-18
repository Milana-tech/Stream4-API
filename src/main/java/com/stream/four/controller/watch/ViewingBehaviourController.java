package com.stream.four.controller.watch;

import com.stream.four.dto.requests.CreateWatchEventRequest;
import com.stream.four.dto.response.watch.WatchEventResponse;
import com.stream.four.service.ViewingBehaviourService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping ("/")
@Tag (name = "Viewing Behaviour", description = "Track user viewing behaviour and progress (supports JSON, XML, CSV)")
public class ViewingBehaviourController
{

    private final ViewingBehaviourService viewingBehaviourService;

    @PostMapping (value = "/watch", produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation (summary = "Record watch event", description = "Record a viewing event with progress. Supports JSON, " +
            "XML, CSV.")
    public WatchEventResponse watch(@Valid @RequestBody CreateWatchEventRequest request, Principal principal)
    {
        return viewingBehaviourService.watch(principal.getName(), request);
    }

    @GetMapping (value = "/history", produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation (summary = "Get watch history", description = "Get user's viewing history. Supports JSON, XML, CSV.")
    public List<WatchEventResponse> history(Principal principal)
    {
        return viewingBehaviourService.history(principal.getName());
    }

    @GetMapping (value = "/progress/{titleId}", produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation (summary = "Get viewing progress", description = "Get viewing progress for a specific title. Supports " +
            "JSON, XML, CSV.")
    public WatchEventResponse progress(@PathVariable String titleId, Principal principal)
    {
        return viewingBehaviourService.progress(principal.getName(), titleId);
    }
}
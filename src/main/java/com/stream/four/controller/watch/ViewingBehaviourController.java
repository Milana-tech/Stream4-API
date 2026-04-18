package com.stream.four.controller.watch;

import com.stream.four.dto.requests.CreateWatchEventRequest;
import com.stream.four.dto.response.watch.WatchEventResponse;
import com.stream.four.exception.ErrorResponse;
import com.stream.four.service.ViewingBehaviourService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/viewing-behaviour")
@Tag(name = "viewing-behaviour", description = "Track user viewing behaviour and progress (supports JSON, XML, CSV)")
public class ViewingBehaviourController {

    private final ViewingBehaviourService viewingBehaviourService;

    @PostMapping(value = "/watch", produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation(summary = "Record watch event", description = "Record a viewing event with progress. Supports JSON, XML, CSV.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Watch event recorded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input - validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<WatchEventResponse> watch(@Valid @RequestBody CreateWatchEventRequest request, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(viewingBehaviourService.watch(principal.getName(), request));
    }

    @GetMapping(value = "/history", produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation(operationId = "getWatchHistory", summary = "Get watch history", description = "Get profile's viewing history. Supports JSON, XML, CSV.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Watch history retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<WatchEventResponse>> history(@RequestParam String profileId, Principal principal) {
        return ResponseEntity.ok(viewingBehaviourService.history(principal.getName(), profileId));
    }

    @GetMapping(value = "/progress/{titleId}", produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation(operationId = "getViewingProgress", summary = "Get viewing progress", description = "Get viewing progress for a specific title. Supports JSON, XML, CSV.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Viewing progress retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "No viewing progress found for this title",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<WatchEventResponse> progress(@PathVariable String titleId, Principal principal) {
        return ResponseEntity.ok(viewingBehaviourService.progress(principal.getName(), titleId));
    }
}

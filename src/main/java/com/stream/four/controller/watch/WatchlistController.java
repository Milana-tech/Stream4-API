package com.stream.four.controller.watch;

import com.stream.four.dto.requests.CreateWatchlistItemRequest;
import com.stream.four.dto.response.watch.WatchlistItemResponse;
import com.stream.four.exception.ErrorResponse;
import com.stream.four.service.WatchlistService;
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
@RequestMapping("/watchlists")
@Tag(name = "watchlists", description = "Manage user watchlist (supports JSON, XML, CSV)")
public class WatchlistController {

    private final WatchlistService watchlistService;

    @PostMapping(produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation(operationId = "addToWatchlist", summary = "Add to watchlist", description = "Add a title to user's watchlist. Supports JSON, XML, CSV.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Title added to watchlist successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input - validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<WatchlistItemResponse> add(@Valid @RequestBody CreateWatchlistItemRequest request, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(watchlistService.add(principal.getName(), request));
    }

    @GetMapping(produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation(operationId = "getWatchlist", summary = "Get watchlist", description = "Get profile's watchlist. Supports JSON, XML, CSV.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Watchlist retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<WatchlistItemResponse>> getAll(@RequestParam String profileId, Principal principal) {
        return ResponseEntity.ok(watchlistService.getAll(principal.getName(), profileId));
    }

    @DeleteMapping("/{id}")
    @Operation(operationId = "removeFromWatchlist", summary = "Remove from watchlist", description = "Remove a title from user's watchlist")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Title removed from watchlist successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Watchlist item not found")
    })
    public ResponseEntity<Void> remove(@PathVariable String id, Principal principal) {
        watchlistService.remove(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }
}

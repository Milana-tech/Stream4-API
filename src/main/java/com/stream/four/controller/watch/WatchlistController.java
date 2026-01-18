package com.stream.four.controller.watch;

import com.stream.four.dto.requests.CreateWatchlistItemRequest;
import com.stream.four.dto.response.watch.WatchlistItemResponse;
import com.stream.four.service.WatchlistService;
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
@RequestMapping ("/watchlist")
@Tag (name = "Watchlist", description = "Manage user watchlist (supports JSON, XML, CSV)")
public class WatchlistController
{

    private final WatchlistService watchlistService;

    @PostMapping (produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation (summary = "Add to watchlist", description = "Add a title to user's watchlist. Supports JSON, XML, CSV.")
    public WatchlistItemResponse add(@Valid @RequestBody CreateWatchlistItemRequest request, Principal principal)
    {
        return watchlistService.add(principal.getName(), request);
    }

    @GetMapping (produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation (summary = "Get watchlist", description = "Get user's watchlist. Supports JSON, XML, CSV.")
    public List<WatchlistItemResponse> getAll(Principal principal)
    {
        return watchlistService.getAll(principal.getName());
    }

    @DeleteMapping ("/{id}")
    @Operation (summary = "Remove from watchlist", description = "Remove a title from user's watchlist")
    public void remove(@PathVariable String id, Principal principal)
    {
        watchlistService.remove(principal.getName(), id);
    }
}
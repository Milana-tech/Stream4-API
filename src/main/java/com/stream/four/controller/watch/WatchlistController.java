package com.stream.four.controller.watch;

import com.stream.four.dto.requests.CreateWatchlistItemRequest;
import com.stream.four.dto.response.watch.WatchlistItemResponse;
import com.stream.four.service.WatchlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;

    @PostMapping
    public WatchlistItemResponse add(@Valid @RequestBody CreateWatchlistItemRequest request, Principal principal) {
        return watchlistService.add(principal.getName(), request);
    }

    @GetMapping
    public List<WatchlistItemResponse> getAll(Principal principal) {
        return watchlistService.getAll(principal.getName());
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable String id, Principal principal) {
        watchlistService.remove(principal.getName(), id);
    }
}

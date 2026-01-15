package com.stream.four.controller;

import com.stream.four.dto.*;
import com.stream.four.mapper.WatchlistMapper;
import com.stream.four.repository.WatchlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/watchlist")
public class WatchlistController {

    private final WatchlistRepository watchlistRepository;
    private final WatchlistMapper watchlistMapper;

    @PostMapping
    public WatchlistItemResponse add(@RequestBody CreateWatchlistItemRequest request, Principal principal) {

        var userId = principal.getName();

        if (watchlistRepository.existsByUserIdAndTitleId(userId, request.getTitleId())) 
        {
            throw new RuntimeException("Title already in watchlist");
        }

        var item = watchlistMapper.toEntity(request);
        item.setUserId(userId);
        item.setAddedAt(System.currentTimeMillis());

        watchlistRepository.save(item);

        return watchlistMapper.toDto(item);
    }

    @GetMapping
    public List<WatchlistItemResponse> getAll(Principal principal) {
        return watchlistRepository.findByUserIdOrderByAddedAtDesc(principal.getName()).stream()
                .map(watchlistMapper::toDto)
                .toList();
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable String id, Principal principal) {

        var item = watchlistRepository.findById(id).orElseThrow(() -> new RuntimeException("Watchlist item not found"));

        if (!item.getUserId().equals(principal.getName())) 
        {
            throw new RuntimeException("Not allowed");
        }

        watchlistRepository.delete(item);
    }
}

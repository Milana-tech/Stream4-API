package com.stream.four.service;

import com.stream.four.dto.requests.CreateWatchlistItemRequest;
import com.stream.four.dto.response.watch.WatchlistItemResponse;
import com.stream.four.mapper.WatchlistMapper;
import com.stream.four.repository.WatchlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final WatchlistMapper watchlistMapper;

    public WatchlistItemResponse add(String userId, CreateWatchlistItemRequest request) {
        if (watchlistRepository.existsByUserIdAndTitleId(userId, request.getTitleId())) {
            throw new RuntimeException("Title already in watchlist");
        }

        var item = watchlistMapper.toEntity(request);
        item.setUserId(userId);
        item.setAddedAt(System.currentTimeMillis());

        watchlistRepository.save(item);
        return watchlistMapper.toDto(item);
    }

    public List<WatchlistItemResponse> getAll(String userId) {
        return watchlistRepository.findByUserIdOrderByAddedAtDesc(userId)
                .stream()
                .map(watchlistMapper::toDto)
                .toList();
    }

    public void remove(String userId, String id) {
        var item = watchlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Watchlist item not found"));

        if (!item.getUserId().equals(userId)) {
            throw new RuntimeException("Not allowed");
        }

        watchlistRepository.delete(item);
    }
}


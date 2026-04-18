package com.stream.four.service;

import com.stream.four.dto.requests.CreateWatchlistItemRequest;
import com.stream.four.dto.response.watch.WatchlistItemResponse;
import com.stream.four.exception.DuplicateResourceException;
import com.stream.four.exception.ResourceNotFoundException;
import com.stream.four.exception.UnauthorizedException;
import com.stream.four.mapper.WatchlistMapper;
import com.stream.four.repository.TitleRepository;
import com.stream.four.repository.WatchlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final WatchlistMapper watchlistMapper;
    private final TitleRepository titleRepository;

    public WatchlistItemResponse add(String userId, CreateWatchlistItemRequest request) {
        String profileId = request.getProfileId();
        if (watchlistRepository.existsByUserIdAndProfileIdAndTitleId(userId, profileId, request.getTitleId())) {
            throw new DuplicateResourceException("Title already in watchlist");
        }

        var item = watchlistMapper.toEntity(request);
        item.setUserId(userId);
        item.setProfileId(profileId);
        item.setAddedAt(System.currentTimeMillis());

        watchlistRepository.save(item);
        return withTitleName(watchlistMapper.toDto(item));
    }

    public List<WatchlistItemResponse> getAll(String userId, String profileId) {
        return watchlistRepository.findByUserIdAndProfileIdOrderByAddedAtDesc(userId, profileId)
                .stream()
                .map(watchlistMapper::toDto)
                .map(this::withTitleName)
                .toList();
    }

    private WatchlistItemResponse withTitleName(WatchlistItemResponse dto) {
        titleRepository.findById(dto.getTitleId())
                .ifPresent(t -> dto.setTitleName(t.getName()));
        return dto;
    }

    public void remove(String userId, String id) {
        var item = watchlistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist item not found"));

        if (!item.getUserId().equals(userId)) {
            throw new UnauthorizedException("Access denied: item does not belong to the current user");
        }

        watchlistRepository.delete(item);
    }
}


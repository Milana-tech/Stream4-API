package com.stream.four.service;

import com.stream.four.dto.requests.CreateWatchEventRequest;
import com.stream.four.dto.response.watch.WatchEventResponse;
import com.stream.four.exception.ResourceNotFoundException;
import com.stream.four.mapper.WatchEventMapper;
import com.stream.four.model.watch.WatchEvent;
import com.stream.four.repository.WatchEventRepository;
import com.stream.four.repository.WatchlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ViewingBehaviourService {

    private final WatchEventRepository watchEventRepository;
    private final WatchEventMapper watchEventMapper;
    private final WatchlistRepository watchlistRepository;

    public WatchEventResponse watch(String userId, CreateWatchEventRequest request) {
        // Check if it's a series's episode
        var existing = request.getEpisodeId() != null
        // If it is, find by user + title + episode
        ? watchEventRepository.findByUserIdAndTitleIdAndEpisodeId(userId, request.getTitleId(), request.getEpisodeId())
        // If not, find by user + title only (movie)
        : watchEventRepository.findByUserIdAndTitleId(userId, request.getTitleId());

        WatchEvent event;

        if (existing.isPresent()) {
            event = existing.get();
            event.setProgressSeconds(request.getProgressSeconds());
            event.setFinished(request.getFinished());
            event.setAutoContinued(request.isAutoContinued());
            event.setLastUpdated(System.currentTimeMillis());
        } else {
            event = watchEventMapper.toEntity(request);
            event.setUserId(userId);
            event.setStartedAt(System.currentTimeMillis());
            event.setLastUpdated(System.currentTimeMillis());
        }

        watchEventRepository.save(event);

        if (event.isFinished()) {
            watchlistRepository.deleteByUserIdAndTitleId(userId, request.getTitleId());
        }

        return watchEventMapper.toDto(event);
    }

    public List<WatchEventResponse> history(String userId) {
        return watchEventRepository.findByUserIdOrderByLastUpdatedDesc(userId)
                .stream()
                .map(watchEventMapper::toDto)
                .toList();
    }

    public WatchEventResponse progress(String userId, String titleId) {
        var event = watchEventRepository.findByUserIdAndTitleId(userId, titleId)
                .orElseThrow(() -> new ResourceNotFoundException("No progress found for this title"));
        return watchEventMapper.toDto(event);
    }
}


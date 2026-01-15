package com.stream.four.service;

import com.stream.four.dto.CreateWatchEventRequest;
import com.stream.four.dto.WatchEventResponse;
import com.stream.four.mapper.WatchEventMapper;
import com.stream.four.model.WatchEvent;
import com.stream.four.repository.WatchEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ViewingBehaviourService {

    private final WatchEventRepository watchEventRepository;
    private final WatchEventMapper watchEventMapper;

    public WatchEventResponse watch(String userId, CreateWatchEventRequest request) {
        var existing = watchEventRepository.findByUserIdAndTitleId(userId, request.getTitleId());

        WatchEvent event;

        if (existing.isPresent()) {
            event = existing.get();
            event.setProgressSeconds(request.getProgressSeconds());
            event.setFinished(request.isFinished());
            event.setLastUpdated(System.currentTimeMillis());
        } else {
            event = watchEventMapper.toEntity(request);
            event.setUserId(userId);
            event.setLastUpdated(System.currentTimeMillis());
        }

        watchEventRepository.save(event);
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
                .orElseThrow(() -> new RuntimeException("No progress found"));
        return watchEventMapper.toDto(event);
    }
}


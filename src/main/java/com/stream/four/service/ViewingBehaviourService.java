package com.stream.four.service;

import com.stream.four.dto.requests.CreateWatchEventRequest;
import com.stream.four.dto.response.watch.WatchEventResponse;
import com.stream.four.exception.ResourceNotFoundException;
import com.stream.four.mapper.WatchEventMapper;
import com.stream.four.model.watch.WatchEvent;
import com.stream.four.repository.TitleRepository;
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
    private final TitleRepository titleRepository;

    public WatchEventResponse watch(String userId, CreateWatchEventRequest request) {
        String profileId = request.getProfileId();
        var existing = request.getEpisodeId() != null
                ? watchEventRepository.findByUserIdAndProfileIdAndTitleIdAndEpisodeId(userId, profileId, request.getTitleId(), request.getEpisodeId())
                : watchEventRepository.findByUserIdAndProfileIdAndTitleId(userId, profileId, request.getTitleId());

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
            event.setProfileId(profileId);
            event.setStartedAt(System.currentTimeMillis());
            event.setLastUpdated(System.currentTimeMillis());
        }

        watchEventRepository.save(event);

        if (event.isFinished()) {
            watchlistRepository.deleteByUserIdAndProfileIdAndTitleId(userId, profileId, request.getTitleId());
        }

        return withTitleName(watchEventMapper.toDto(event));
    }

    public List<WatchEventResponse> history(String userId, String profileId) {
        return watchEventRepository.findByUserIdAndProfileIdOrderByLastUpdatedDesc(userId, profileId)
                .stream()
                .map(watchEventMapper::toDto)
                .map(this::withTitleName)
                .toList();
    }

    public WatchEventResponse progress(String userId, String titleId) {
        var event = watchEventRepository.findByUserIdAndTitleId(userId, titleId)
                .orElseThrow(() -> new ResourceNotFoundException("No progress found for this title"));
        return withTitleName(watchEventMapper.toDto(event));
    }

    private WatchEventResponse withTitleName(WatchEventResponse dto) {
        titleRepository.findById(dto.getTitleId())
                .ifPresent(t -> dto.setTitleName(t.getName()));
        return dto;
    }
}


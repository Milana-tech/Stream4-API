package com.stream.four.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stream.four.dto.CreateWatchEventRequest;
import com.stream.four.dto.WatchEventResponse;
import com.stream.four.mapper.WatchEventMapper;
import com.stream.four.model.WatchEvent;
import com.stream.four.repository.WatchEventRepository;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class ViewingBehaviourController {

    private final WatchEventRepository watchEventRepository;
    private final WatchEventMapper watchEventMapper;

    @PostMapping("/watch")
    public WatchEventResponse watch(@RequestBody CreateWatchEventRequest request, Principal principal) {

        var userId = principal.getName();

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

    @GetMapping("/history")
    public List<WatchEventResponse> history(Principal principal) {
        return watchEventRepository.findByUserIdOrderByLastUpdatedDesc(principal.getName()).stream()
                .map(watchEventMapper::toDto)
                .toList();
    }

    @GetMapping("/progress/{titleId}")
    public WatchEventResponse progress(@PathVariable String titleId, Principal principal) {
        var event = watchEventRepository.findByUserIdAndTitleId(principal.getName(), titleId)
                .orElseThrow(() -> new RuntimeException("No progress found"));
                
        return watchEventMapper.toDto(event);
    }
}

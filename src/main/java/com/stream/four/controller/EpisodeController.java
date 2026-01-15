package com.stream.four.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stream.four.dto.CreateEpisodeRequest;
import com.stream.four.dto.EpisodeResponse;
import com.stream.four.service.EpisodeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seasons/{seasonId}/episodes")
public class EpisodeController {

    private final EpisodeService episodeService;

    @PostMapping
    public EpisodeResponse create(@PathVariable String seasonId, @Valid @RequestBody CreateEpisodeRequest request) {
        return episodeService.createEpisode(seasonId, request);
    }

    @GetMapping
    public List<EpisodeResponse> getAll(@PathVariable String seasonId) {
        return episodeService.getEpisodesForSeason(seasonId);
    }
}

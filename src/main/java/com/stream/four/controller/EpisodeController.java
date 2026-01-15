package com.stream.four.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stream.four.dto.CreateEpisodeRequest;
import com.stream.four.dto.EpisodeResponse;
import com.stream.four.mapper.EpisodeMapper;
import com.stream.four.repository.EpisodeRepository;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seasons/{seasonId}/episodes")
public class EpisodeController {

    private final EpisodeRepository episodeRepository;
    private final EpisodeMapper episodeMapper;

    @PostMapping
    public EpisodeResponse create(@PathVariable String seasonId, @RequestBody CreateEpisodeRequest request) {
        var episode = episodeMapper.toEntity(request);
        episode.setSeasonId(seasonId);
        episodeRepository.save(episode);
        
        return episodeMapper.toDto(episode);
    }

    @GetMapping
    public List<EpisodeResponse> getAll(@PathVariable String seasonId) {
        return episodeRepository.findBySeasonIdAndDeletedFalse(seasonId).stream().map(episodeMapper::toDto).toList();
    }
}

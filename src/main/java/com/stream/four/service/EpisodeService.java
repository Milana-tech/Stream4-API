package com.stream.four.service;

import com.stream.four.dto.requests.CreateEpisodeRequest;
import com.stream.four.dto.response.watch.EpisodeResponse;
import com.stream.four.mapper.EpisodeMapper;
import com.stream.four.repository.EpisodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EpisodeService {

    private final EpisodeRepository episodeRepository;
    private final EpisodeMapper episodeMapper;

    public EpisodeResponse createEpisode(String seasonId, CreateEpisodeRequest request) {
        var episode = episodeMapper.toEntity(request);
        episode.setSeasonId(seasonId);
        episodeRepository.save(episode);
        return episodeMapper.toDto(episode);
    }

    public List<EpisodeResponse> getEpisodesForSeason(String seasonId) {
        return episodeRepository.findBySeasonIdAndDeletedFalse(seasonId)
                .stream()
                .map(episodeMapper::toDto)
                .toList();
    }
}


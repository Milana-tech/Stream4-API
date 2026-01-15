package com.stream.four.service;

import com.stream.four.dto.CreateSeasonRequest;
import com.stream.four.dto.SeasonResponse;
import com.stream.four.mapper.SeasonMapper;
import com.stream.four.repository.SeasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeasonService {

    private final SeasonRepository seasonRepository;
    private final SeasonMapper seasonMapper;

    public SeasonResponse createSeason(String titleId, CreateSeasonRequest request) {
        var season = seasonMapper.toEntity(request);
        season.setTitleId(titleId);
        seasonRepository.save(season);
        return seasonMapper.toDto(season);
    }

    public List<SeasonResponse> getSeasonsForTitle(String titleId) {
        return seasonRepository.findByTitleIdAndDeletedFalse(titleId)
                .stream()
                .map(seasonMapper::toDto)
                .toList();
    }
}

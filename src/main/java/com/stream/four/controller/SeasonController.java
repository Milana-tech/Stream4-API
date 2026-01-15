package com.stream.four.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stream.four.dto.CreateSeasonRequest;
import com.stream.four.dto.SeasonResponse;
import com.stream.four.mapper.SeasonMapper;
import com.stream.four.repository.SeasonRepository;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/titles/{titleId}/seasons")
public class SeasonController {

    private final SeasonRepository seasonRepository;
    private final SeasonMapper seasonMapper;

    @PostMapping
    public SeasonResponse create(@PathVariable String titleId, @RequestBody CreateSeasonRequest request) {
        var season = seasonMapper.toEntity(request);
        season.setTitleId(titleId);
        seasonRepository.save(season);
        
        return seasonMapper.toDto(season);
    }

    @GetMapping
    public List<SeasonResponse> getAll(@PathVariable String titleId) {
        return seasonRepository.findByTitleIdAndDeletedFalse(titleId).stream().map(seasonMapper::toDto).toList();
    }
}

package com.stream.four.controller;

import com.stream.four.dto.CreateSeasonRequest;
import com.stream.four.dto.SeasonResponse;
import com.stream.four.service.SeasonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/titles/{titleId}/seasons")
public class SeasonController {

    private final SeasonService seasonService;

    @PostMapping
    public SeasonResponse create(@PathVariable String titleId, @Valid @RequestBody CreateSeasonRequest request) {
        return seasonService.createSeason(titleId, request);
    }

    @GetMapping
    public List<SeasonResponse> getAll(@PathVariable String titleId) {
        return seasonService.getSeasonsForTitle(titleId);
    }
}

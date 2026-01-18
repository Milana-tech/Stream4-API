package com.stream.four.controller.watch;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stream.four.dto.requests.CreateEpisodeRequest;
import com.stream.four.dto.response.watch.EpisodeResponse;
import com.stream.four.service.EpisodeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping ("/seasons/{seasonId}/episodes")
@Tag (name = "Episodes", description = "Manage series episodes (supports JSON, XML, CSV)")
public class EpisodeController
{

    private final EpisodeService episodeService;

    @PostMapping (produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation (summary = "Create episode", description = "Create a new episode for a season. Supports JSON, XML, CSV.")
    public EpisodeResponse create(@PathVariable String seasonId, @Valid @RequestBody CreateEpisodeRequest request)
    {
        return episodeService.createEpisode(seasonId, request);
    }

    @GetMapping (produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation (summary = "Get episodes", description = "Get all episodes for a season. Supports JSON, XML, CSV.")
    public List<EpisodeResponse> getAll(@PathVariable String seasonId)
    {
        return episodeService.getEpisodesForSeason(seasonId);
    }
}
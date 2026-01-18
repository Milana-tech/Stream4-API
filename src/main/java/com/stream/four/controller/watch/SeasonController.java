package com.stream.four.controller.watch;

import com.stream.four.dto.requests.CreateSeasonRequest;
import com.stream.four.dto.response.watch.SeasonResponse;
import com.stream.four.service.SeasonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping ("/titles/{titleId}/seasons")
@Tag (name = "Seasons", description = "Manage series seasons (supports JSON, XML, CSV)")
public class SeasonController
{

    private final SeasonService seasonService;

    @PostMapping (produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation (summary = "Create season", description = "Create a new season for a series. Supports JSON, XML, CSV.")
    public SeasonResponse create(@PathVariable String titleId, @Valid @RequestBody CreateSeasonRequest request)
    {
        return seasonService.createSeason(titleId, request);
    }

    @GetMapping (produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation (summary = "Get seasons", description = "Get all seasons for a series. Supports JSON, XML, CSV.")
    public List<SeasonResponse> getAll(@PathVariable String titleId)
    {
        return seasonService.getSeasonsForTitle(titleId);
    }
}
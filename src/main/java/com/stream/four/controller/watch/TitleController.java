package com.stream.four.controller.watch;

import com.stream.four.dto.requests.CreateTitleRequest;
import com.stream.four.dto.response.watch.TitleResponse;
import com.stream.four.service.TitleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/titles")
@Tag(name = "titles", description = "Endpoints to manage movies and series (supports JSON, XML, CSV)")
public class TitleController {

    private final TitleService titleService;

    @PostMapping(produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation(summary = "Create title", description = "Create a new movie or series")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Title created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input - validation failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid")
    })
    public ResponseEntity<TitleResponse> create(@Valid @RequestBody CreateTitleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(titleService.createTitle(request));
    }

    @GetMapping(produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation(summary = "Get all titles", description = "Get all movies and series. Supports JSON, XML, CSV.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Titles retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid")
    })
    public ResponseEntity<List<TitleResponse>> getAll() {
        return ResponseEntity.ok(titleService.getAllTitles());
    }

    @GetMapping(value = "/{id}", produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation(summary = "Get title by ID", description = "Get specific movie or series. Supports JSON, XML, CSV.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Title retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Title not found")
    })
    public ResponseEntity<TitleResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(titleService.getTitleById(id));
    }

    @GetMapping(value = "/for-profile/{profileId}", produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation(summary = "Get titles for profile", description = "Get titles filtered by profile age restrictions and viewing preferences.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Filtered titles retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public ResponseEntity<List<TitleResponse>> getForProfile(@PathVariable String profileId) {
        return ResponseEntity.ok(titleService.getTitlesForProfile(profileId));
    }
}
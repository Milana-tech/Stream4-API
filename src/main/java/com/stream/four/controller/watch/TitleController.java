package com.stream.four.controller.watch;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.stream.four.dto.requests.CreateTitleRequest;
import com.stream.four.dto.response.watch.TitleResponse;
import com.stream.four.service.TitleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping ("/titles")
@Tag (name = "Titles", description = "Endpoints to manage movies and series (supports JSON, XML, CSV)")
public class TitleController
{

    private final TitleService titleService;

    @PostMapping (produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation (summary = "Create title", description = "Create a new movie or series")
    public TitleResponse create(@Valid @RequestBody CreateTitleRequest request)
    {
        return titleService.createTitle(request);
    }

    @GetMapping (produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation (summary = "Get all titles", description = "Get all movies and series. Supports JSON, XML, CSV.")
    public List<TitleResponse> getAll()
    {
        return titleService.getAllTitles();
    }

    @GetMapping (value = "/{id}", produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation (summary = "Get title by ID", description = "Get specific movie or series. Supports JSON, XML, CSV.")
    public TitleResponse getById(@PathVariable String id)
    {
        return titleService.getTitleById(id);
    }
}
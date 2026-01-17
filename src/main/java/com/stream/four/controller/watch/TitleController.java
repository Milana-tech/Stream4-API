package com.stream.four.controller.watch;

import java.util.List;

import jakarta.validation.Valid;
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
@RequestMapping("/titles")
public class TitleController {

    private final TitleService titleService;

    @PostMapping
    public TitleResponse create(@Valid @RequestBody CreateTitleRequest request)
    {
        return titleService.createTitle(request);
    }

    @GetMapping
    public List<TitleResponse> getAll()
    {
        return titleService.getAllTitles();
    }

    @GetMapping("/{id}")
    public TitleResponse getById(@PathVariable String id)
    {
        return titleService.getTitleById(id);
    }
}

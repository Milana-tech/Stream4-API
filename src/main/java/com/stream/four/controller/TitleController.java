package com.stream.four.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stream.four.dto.CreateTitleRequest;
import com.stream.four.dto.TitleResponse;
import com.stream.four.mapper.TitleMapper;
import com.stream.four.repository.TitleRepository;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/titles")
public class TitleController {
    
    private final TitleRepository titleRepository;
    private final TitleMapper titleMapper;

    @PostMapping
    public TitleResponse create(@RequestBody CreateTitleRequest request)
    {
        var title = titleMapper.toEntity(request);
        titleRepository.save(title);
        
        return titleMapper.toDto(title);
    }

    @GetMapping
    public List<TitleResponse> getAll()
    {
        return titleRepository.findByDeletedFalse().stream().map(titleMapper::toDto).toList();
    }

    @GetMapping
    public TitleResponse getById(@PathVariable String id)
    {
        var title = titleRepository.findById(id).orElseThrow(() -> new RuntimeException("Title not found"));

        return titleMapper.toDto(title);
    }
}

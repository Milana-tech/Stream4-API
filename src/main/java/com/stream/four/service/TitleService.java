package com.stream.four.service;

import com.stream.four.dto.CreateTitleRequest;
import com.stream.four.dto.TitleResponse;
import com.stream.four.mapper.TitleMapper;
import com.stream.four.repository.TitleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TitleService {

    private final TitleRepository titleRepository;
    private final TitleMapper titleMapper;

    public TitleResponse createTitle(CreateTitleRequest request) {
        var title = titleMapper.toEntity(request);
        titleRepository.save(title);
        return titleMapper.toDto(title);
    }

    public List<TitleResponse> getAllTitles() {
        return titleRepository.findByDeletedFalse()
                .stream()
                .map(titleMapper::toDto)
                .toList();
    }

    public TitleResponse getTitleById(String id) {
        var title = titleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Title not found"));
        return titleMapper.toDto(title);
    }
}


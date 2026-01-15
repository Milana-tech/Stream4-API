package com.stream.four.service;

import com.stream.four.dto.CreateTitleRequest;
import com.stream.four.dto.TitleResponse;
import com.stream.four.mapper.TitleMapper;
import com.stream.four.model.Title;
import com.stream.four.repository.TitleRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TitleServiceTest {

    private final TitleRepository titleRepository = mock(TitleRepository.class);
    private final TitleMapper titleMapper = mock(TitleMapper.class);

    private final TitleService titleService = new TitleService(titleRepository, titleMapper);

    @Test
    void createTitle_mapsSavesAndReturnsDto() {
        var req = new CreateTitleRequest();
        var entity = new Title();
        var dto = new TitleResponse();

        when(titleMapper.toEntity(req)).thenReturn(entity);
        when(titleMapper.toDto(entity)).thenReturn(dto);

        var result = titleService.createTitle(req);

        assertSame(dto, result);
        verify(titleRepository).save(entity);
    }

    @Test
    void getAllTitles_mapsToDtos() {
        var t1 = new Title();
        var t2 = new Title();

        when(titleRepository.findByDeletedFalse()).thenReturn(List.of(t1, t2));
        when(titleMapper.toDto(t1)).thenReturn(new TitleResponse());
        when(titleMapper.toDto(t2)).thenReturn(new TitleResponse());

        var result = titleService.getAllTitles();

        assertEquals(2, result.size());
        verify(titleRepository).findByDeletedFalse();
    }

    @Test
    void getTitleById_missing_throws() {
        when(titleRepository.findById("id")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> titleService.getTitleById("id"));
    }
}


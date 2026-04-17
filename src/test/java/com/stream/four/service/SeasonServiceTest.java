package com.stream.four.service;

import com.stream.four.dto.requests.CreateSeasonRequest;
import com.stream.four.dto.response.watch.SeasonResponse;
import com.stream.four.mapper.SeasonMapper;
import com.stream.four.model.watch.Season;
import com.stream.four.repository.SeasonRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class SeasonServiceTest {

    private final SeasonRepository seasonRepository = mock(SeasonRepository.class);
    private final SeasonMapper seasonMapper = mock(SeasonMapper.class);

    private final SeasonService seasonService = new SeasonService(seasonRepository, seasonMapper);

    @Test
    void createSeason_setsTitleId_savesAndReturnsDto() {
        var req = new CreateSeasonRequest();
        var entity = new Season();
        var dto = new SeasonResponse();

        when(seasonMapper.toEntity(req)).thenReturn(entity);
        when(seasonMapper.toDto(entity)).thenReturn(dto);

        var result = seasonService.createSeason("t1", req);

        assertSame(dto, result);
        assertEquals("t1", entity.getTitleId());
        verify(seasonRepository).save(entity);
    }

    @Test
    void getSeasonsForTitle_mapsToDtos() {
        var s1 = new Season();
        var s2 = new Season();

        when(seasonRepository.findByTitleIdAndDeletedFalse("t")).thenReturn(List.of(s1, s2));
        when(seasonMapper.toDto(s1)).thenReturn(new SeasonResponse());
        when(seasonMapper.toDto(s2)).thenReturn(new SeasonResponse());

        var result = seasonService.getSeasonsForTitle("t");

        assertEquals(2, result.size());
        verify(seasonRepository).findByTitleIdAndDeletedFalse("t");
    }
}

package com.stream.four.service;

import com.stream.four.dto.CreateEpisodeRequest;
import com.stream.four.dto.EpisodeResponse;
import com.stream.four.mapper.EpisodeMapper;
import com.stream.four.model.Episode;
import com.stream.four.repository.EpisodeRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class EpisodeServiceTest {

    private final EpisodeRepository episodeRepository = mock(EpisodeRepository.class);
    private final EpisodeMapper episodeMapper = mock(EpisodeMapper.class);

    private final EpisodeService episodeService = new EpisodeService(episodeRepository, episodeMapper);

    @Test
    void createEpisode_setsSeasonId_savesAndReturnsDto() {
        var req = new CreateEpisodeRequest();
        var entity = new Episode();
        var dto = new EpisodeResponse();

        when(episodeMapper.toEntity(req)).thenReturn(entity);
        when(episodeMapper.toDto(entity)).thenReturn(dto);

        var result = episodeService.createEpisode("s1", req);

        assertSame(dto, result);
        assertEquals("s1", entity.getSeasonId());
        verify(episodeRepository).save(entity);
    }

    @Test
    void getEpisodesForSeason_mapsToDtos() {
        var e1 = new Episode();
        var e2 = new Episode();

        when(episodeRepository.findBySeasonIdAndDeletedFalse("s")).thenReturn(List.of(e1, e2));
        when(episodeMapper.toDto(e1)).thenReturn(new EpisodeResponse());
        when(episodeMapper.toDto(e2)).thenReturn(new EpisodeResponse());

        var result = episodeService.getEpisodesForSeason("s");

        assertEquals(2, result.size());
        verify(episodeRepository).findBySeasonIdAndDeletedFalse("s");
    }
}

package com.stream.four.service;

import com.stream.four.dto.requests.CreateWatchEventRequest;
import com.stream.four.dto.response.watch.WatchEventResponse;
import com.stream.four.mapper.WatchEventMapper;
import com.stream.four.model.watch.WatchEvent;
import com.stream.four.repository.TitleRepository;
import com.stream.four.repository.WatchEventRepository;
import com.stream.four.repository.WatchlistRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ViewingBehaviourServiceTest {

    private final WatchEventRepository watchEventRepository = mock(WatchEventRepository.class);
    private final WatchEventMapper watchEventMapper = mock(WatchEventMapper.class);
    private final WatchlistRepository watchlistRepository = mock(WatchlistRepository.class);
    private final TitleRepository titleRepository = mock(TitleRepository.class);

    private final ViewingBehaviourService service = new ViewingBehaviourService(watchEventRepository, watchEventMapper, watchlistRepository, titleRepository);

    @Test
    void watch_existingMovie_updatesProgressAndReturnsDto() {
        var req = new CreateWatchEventRequest();
        req.setTitleId("t1");
        req.setProfileId("p1");
        req.setProgressSeconds(120);
        req.setFinished(false);

        var existing = new WatchEvent();
        existing.setUserId("u1");
        existing.setTitleId("t1");
        existing.setProgressSeconds(60);
        existing.setFinished(false);
        existing.setLastUpdated(1L);

        when(watchEventRepository.findByUserIdAndProfileIdAndTitleId("u1", "p1", "t1")).thenReturn(Optional.of(existing));
        when(watchEventMapper.toDto(existing)).thenReturn(new WatchEventResponse());

        var result = service.watch("u1", req);

        assertNotNull(result);
        assertEquals(120, existing.getProgressSeconds());
        assertFalse(existing.isFinished());
        assertTrue(existing.getLastUpdated() > 1L);
        verify(watchEventRepository).save(existing);
        verify(watchEventMapper, never()).toEntity(any());
    }

    @Test
    void watch_newMovie_setsStartedAtAndSaves() {
        var req = new CreateWatchEventRequest();
        req.setTitleId("t1");
        req.setProfileId("p1");
        req.setProgressSeconds(0);
        req.setFinished(false);

        var newEvent = new WatchEvent();
        when(watchEventRepository.findByUserIdAndProfileIdAndTitleId("u1", "p1", "t1")).thenReturn(Optional.empty());
        when(watchEventMapper.toEntity(req)).thenReturn(newEvent);
        when(watchEventMapper.toDto(newEvent)).thenReturn(new WatchEventResponse());

        service.watch("u1", req);

        assertTrue(newEvent.getStartedAt() > 0);
        assertEquals("u1", newEvent.getUserId());
        verify(watchEventRepository).save(newEvent);
    }

    @Test
    void watch_existingEpisode_usesEpisodeLookup() {
        var req = new CreateWatchEventRequest();
        req.setTitleId("t1");
        req.setProfileId("p1");
        req.setEpisodeId("e1");
        req.setProgressSeconds(300);
        req.setFinished(false);
        req.setAutoContinued(true);

        var existing = new WatchEvent();
        existing.setProgressSeconds(100);
        existing.setLastUpdated(1L);

        when(watchEventRepository.findByUserIdAndProfileIdAndTitleIdAndEpisodeId("u1", "p1", "t1", "e1")).thenReturn(Optional.of(existing));
        when(watchEventMapper.toDto(existing)).thenReturn(new WatchEventResponse());

        service.watch("u1", req);

        assertEquals(300, existing.getProgressSeconds());
        assertTrue(existing.isAutoContinued());
        verify(watchEventRepository).save(existing);
        verify(watchEventRepository, never()).findByUserIdAndProfileIdAndTitleId(any(), any(), any());
    }

    @Test
    void watch_finished_removesFromWatchlist() {
        var req = new CreateWatchEventRequest();
        req.setTitleId("t1");
        req.setProfileId("p1");
        req.setProgressSeconds(5400);
        req.setFinished(true);

        var existing = new WatchEvent();
        when(watchEventRepository.findByUserIdAndProfileIdAndTitleId("u1", "p1", "t1")).thenReturn(Optional.of(existing));
        when(watchEventMapper.toDto(existing)).thenReturn(new WatchEventResponse());

        service.watch("u1", req);

        verify(watchlistRepository).deleteByUserIdAndProfileIdAndTitleId("u1", "p1", "t1");
    }

    @Test
    void watch_notFinished_doesNotRemoveFromWatchlist() {
        var req = new CreateWatchEventRequest();
        req.setTitleId("t1");
        req.setProfileId("p1");
        req.setProgressSeconds(100);
        req.setFinished(false);

        var existing = new WatchEvent();
        when(watchEventRepository.findByUserIdAndProfileIdAndTitleId("u1", "p1", "t1")).thenReturn(Optional.of(existing));
        when(watchEventMapper.toDto(existing)).thenReturn(new WatchEventResponse());

        service.watch("u1", req);

        verify(watchlistRepository, never()).deleteByUserIdAndProfileIdAndTitleId(any(), any(), any());
    }

    @Test
    void history_returnsAllEventsForUser() {
        var e1 = new WatchEvent();
        var e2 = new WatchEvent();
        when(watchEventRepository.findByUserIdAndProfileIdOrderByLastUpdatedDesc("u1", "p1")).thenReturn(List.of(e1, e2));
        when(watchEventMapper.toDto(e1)).thenReturn(new WatchEventResponse());
        when(watchEventMapper.toDto(e2)).thenReturn(new WatchEventResponse());

        var result = service.history("u1", "p1");

        assertEquals(2, result.size());
        verify(watchEventRepository).findByUserIdAndProfileIdOrderByLastUpdatedDesc("u1", "p1");
    }

    @Test
    void progress_found_returnsDto() {
        var event = new WatchEvent();
        var dto = new WatchEventResponse();
        when(watchEventRepository.findByUserIdAndTitleId("u1", "t1")).thenReturn(Optional.of(event));
        when(watchEventMapper.toDto(event)).thenReturn(dto);

        var result = service.progress("u1", "t1");

        assertSame(dto, result);
    }

    @Test
    void progress_missing_throws() {
        when(watchEventRepository.findByUserIdAndTitleId("u1", "t1")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.progress("u1", "t1"));
    }
}

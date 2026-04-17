package com.stream.four.service;

import com.stream.four.dto.requests.CreateWatchEventRequest;
import com.stream.four.dto.response.watch.WatchEventResponse;
import com.stream.four.mapper.WatchEventMapper;
import com.stream.four.model.watch.WatchEvent;
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

    private final ViewingBehaviourService service = new ViewingBehaviourService(watchEventRepository, watchEventMapper, watchlistRepository);

    @Test
    void watch_existingMovie_updatesProgressAndReturnsDto() {
        // Arrange
        var req = new CreateWatchEventRequest();
        req.setTitleId("t1");
        req.setProgressSeconds(120);
        req.setFinished(false);

        var existing = new WatchEvent();
        existing.setUserId("u1");
        existing.setTitleId("t1");
        existing.setProgressSeconds(60);
        existing.setFinished(false);
        existing.setLastUpdated(1L);

        // Act
        when(watchEventRepository.findByUserIdAndTitleId("u1", "t1")).thenReturn(Optional.of(existing));
        when(watchEventMapper.toDto(existing)).thenReturn(new WatchEventResponse());

        var result = service.watch("u1", req);

        // Assert
        assertNotNull(result);
        assertEquals(120, existing.getProgressSeconds());
        assertFalse(existing.isFinished());
        assertTrue(existing.getLastUpdated() > 1L);
        verify(watchEventRepository).save(existing);
        verify(watchEventMapper, never()).toEntity(any());
    }

    @Test
    void watch_newMovie_setsStartedAtAndSaves() {
        // Arrange
        var req = new CreateWatchEventRequest();
        req.setTitleId("t1");
        req.setProgressSeconds(0);
        req.setFinished(false);

        var newEvent = new WatchEvent();
        when(watchEventRepository.findByUserIdAndTitleId("u1", "t1")).thenReturn(Optional.empty());
        when(watchEventMapper.toEntity(req)).thenReturn(newEvent);
        when(watchEventMapper.toDto(newEvent)).thenReturn(new WatchEventResponse());

        // Act
        service.watch("u1", req);

        // Assert
        assertTrue(newEvent.getStartedAt() > 0);
        assertEquals("u1", newEvent.getUserId());
        verify(watchEventRepository).save(newEvent);
    }

    @Test
    void watch_existingEpisode_usesEpisodeLookup() {
        // Arrange
        var req = new CreateWatchEventRequest();
        req.setTitleId("t1");
        req.setEpisodeId("e1");
        req.setProgressSeconds(300);
        req.setFinished(false);
        req.setAutoContinued(true);

        var existing = new WatchEvent();
        existing.setProgressSeconds(100);
        existing.setLastUpdated(1L);

        when(watchEventRepository.findByUserIdAndTitleIdAndEpisodeId("u1", "t1", "e1")).thenReturn(Optional.of(existing));
        when(watchEventMapper.toDto(existing)).thenReturn(new WatchEventResponse());

        // Act
        service.watch("u1", req);

        // Assert
        assertEquals(300, existing.getProgressSeconds());
        assertTrue(existing.isAutoContinued());
        verify(watchEventRepository).save(existing);
        verify(watchEventRepository, never()).findByUserIdAndTitleId(any(), any());
    }

    @Test
    void watch_finished_removesFromWatchlist() {
        // Arrange
        var req = new CreateWatchEventRequest();
        req.setTitleId("t1");
        req.setProgressSeconds(5400);
        req.setFinished(true);

        var existing = new WatchEvent();
        when(watchEventRepository.findByUserIdAndTitleId("u1", "t1")).thenReturn(Optional.of(existing));
        when(watchEventMapper.toDto(existing)).thenReturn(new WatchEventResponse());

        // Act
        service.watch("u1", req);

        // Assert
        verify(watchlistRepository).deleteByUserIdAndTitleId("u1", "t1");
    }

    @Test
    void watch_notFinished_doesNotRemoveFromWatchlist() {
        // Arrange
        var req = new CreateWatchEventRequest();
        req.setTitleId("t1");
        req.setProgressSeconds(100);
        req.setFinished(false);

        var existing = new WatchEvent();
        when(watchEventRepository.findByUserIdAndTitleId("u1", "t1")).thenReturn(Optional.of(existing));
        when(watchEventMapper.toDto(existing)).thenReturn(new WatchEventResponse());

        // Act
        service.watch("u1", req);

        // Assert
        verify(watchlistRepository, never()).deleteByUserIdAndTitleId(any(), any());
    }

    @Test
    void history_returnsAllEventsForUser() {
        // Arrange
        var e1 = new WatchEvent();
        var e2 = new WatchEvent();
        when(watchEventRepository.findByUserIdOrderByLastUpdatedDesc("u1")).thenReturn(List.of(e1, e2));
        when(watchEventMapper.toDto(e1)).thenReturn(new WatchEventResponse());
        when(watchEventMapper.toDto(e2)).thenReturn(new WatchEventResponse());

        // Act
        var result = service.history("u1");

        // Assert
        assertEquals(2, result.size());
        verify(watchEventRepository).findByUserIdOrderByLastUpdatedDesc("u1");
    }

    @Test
    void progress_found_returnsDto() {
        // Arrange
        var event = new WatchEvent();
        var dto = new WatchEventResponse();
        when(watchEventRepository.findByUserIdAndTitleId("u1", "t1")).thenReturn(Optional.of(event));
        when(watchEventMapper.toDto(event)).thenReturn(dto);

        // Act
        var result = service.progress("u1", "t1");

        // Assert
        assertSame(dto, result);
    }

    @Test
    void progress_missing_throws() {
        when(watchEventRepository.findByUserIdAndTitleId("u1", "t1")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.progress("u1", "t1"));
    }
}


package com.stream.four.service;

import com.stream.four.dto.requests.CreateWatchEventRequest;
import com.stream.four.dto.response.watch.WatchEventResponse;
import com.stream.four.mapper.WatchEventMapper;
import com.stream.four.model.watch.WatchEvent;
import com.stream.four.repository.WatchEventRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ViewingBehaviourServiceTest {

    private final WatchEventRepository watchEventRepository = mock(WatchEventRepository.class);
    private final WatchEventMapper watchEventMapper = mock(WatchEventMapper.class);

    private final ViewingBehaviourService service = new ViewingBehaviourService(watchEventRepository, watchEventMapper);

    @Test
    void watch_whenExisting_updatesFieldsAndReturnsDto() {
        var req = new CreateWatchEventRequest();
        req.setTitleId("t");
        req.setProgressSeconds(10);
        req.setFinished(true);

        var existing = new WatchEvent();
        existing.setUserId("u");
        existing.setTitleId("t");
        existing.setProgressSeconds(1);
        existing.setFinished(false);
        existing.setLastUpdated(1L);

        when(watchEventRepository.findByUserIdAndTitleId("u", "t")).thenReturn(Optional.of(existing));
        when(watchEventMapper.toDto(existing)).thenReturn(new WatchEventResponse());

        var result = service.watch("u", req);

        assertNotNull(result);
        assertEquals(10, existing.getProgressSeconds());
        assertTrue(existing.isFinished());
        assertTrue(existing.getLastUpdated() > 1L);
        verify(watchEventRepository).save(existing);
        verify(watchEventMapper, never()).toEntity(any());
    }

    @Test
    void progress_missing_throws() {
        when(watchEventRepository.findByUserIdAndTitleId("u", "t")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.progress("u", "t"));
    }
}


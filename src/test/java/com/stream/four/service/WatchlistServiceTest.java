package com.stream.four.service;

import com.stream.four.dto.requests.CreateWatchlistItemRequest;
import com.stream.four.dto.response.watch.WatchlistItemResponse;
import com.stream.four.mapper.WatchlistMapper;
import com.stream.four.model.watch.WatchlistItem;
import com.stream.four.repository.WatchlistRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WatchlistServiceTest {

    private final WatchlistRepository watchlistRepository = mock(WatchlistRepository.class);
    private final WatchlistMapper watchlistMapper = mock(WatchlistMapper.class);

    private final WatchlistService watchlistService = new WatchlistService(watchlistRepository, watchlistMapper);

    @Test
    void add_whenAlreadyExists_throws() {
        var req = new CreateWatchlistItemRequest();
        req.setTitleId("t");

        when(watchlistRepository.existsByUserIdAndTitleId("u", "t")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> watchlistService.add("u", req));
        verify(watchlistRepository, never()).save(any());
    }

    @Test
    void remove_whenNotOwner_throws() {
        var item = new WatchlistItem();
        item.setId("id");
        item.setUserId("other");

        when(watchlistRepository.findById("id")).thenReturn(Optional.of(item));

        assertThrows(RuntimeException.class, () -> watchlistService.remove("u", "id"));
        verify(watchlistRepository, never()).delete(any());
    }

    @Test
    void add_setsUserIdAndAddedAt_andReturnsDto() {
        var req = new CreateWatchlistItemRequest();
        req.setTitleId("t");

        when(watchlistRepository.existsByUserIdAndTitleId("u", "t")).thenReturn(false);

        var entity = new WatchlistItem();
        var dto = new WatchlistItemResponse();

        when(watchlistMapper.toEntity(req)).thenReturn(entity);
        when(watchlistMapper.toDto(entity)).thenReturn(dto);

        var result = watchlistService.add("u", req);

        assertSame(dto, result);
        assertEquals("u", entity.getUserId());
        assertTrue(entity.getAddedAt() > 0);
        verify(watchlistRepository).save(entity);
    }
}

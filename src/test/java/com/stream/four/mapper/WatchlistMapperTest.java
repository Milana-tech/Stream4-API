package com.stream.four.mapper;

import com.stream.four.dto.requests.CreateWatchlistItemRequest;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class WatchlistMapperTest {

    private final WatchlistMapper mapper = Mappers.getMapper(WatchlistMapper.class);

    @Test
    void toEntity_mapsBasicFields() {
        var req = new CreateWatchlistItemRequest();
        req.setTitleId("t");

        var entity = mapper.toEntity(req);

        assertNotNull(entity);
        assertEquals("t", entity.getTitleId());
    }
}


package com.stream.four.mapper;

import com.stream.four.dto.CreateWatchEventRequest;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class WatchEventMapperTest {

    private final WatchEventMapper mapper = Mappers.getMapper(WatchEventMapper.class);

    @Test
    void toEntity_mapsBasicFields() {
        var req = new CreateWatchEventRequest();
        req.setTitleId("t");
        req.setProgressSeconds(12);
        req.setFinished(true);

        var entity = mapper.toEntity(req);

        assertNotNull(entity);
        assertEquals("t", entity.getTitleId());
        assertEquals(12, entity.getProgressSeconds());
        assertTrue(entity.isFinished());
    }
}


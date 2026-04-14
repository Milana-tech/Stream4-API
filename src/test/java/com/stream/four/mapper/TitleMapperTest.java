package com.stream.four.mapper;

import com.stream.four.dto.requests.CreateTitleRequest;
import com.stream.four.model.enums.VideoQuality;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TitleMapperTest {

    private final TitleMapper mapper = Mappers.getMapper(TitleMapper.class);

    @Test
    void toEntity_mapsBasicFields() {
        var req = new CreateTitleRequest();
        req.setName("Title");

        var entity = mapper.toEntity(req);

        assertNotNull(entity);
        assertEquals("Title", entity.getName());
    }

    @Test
    void toEntity_mapsSupportedQualitiesAndDuration() {
        var req = new CreateTitleRequest();
        req.setName("Movie");
        req.setDurationSeconds(5400);
        req.setSupportedQualities(Set.of(VideoQuality.HD, VideoQuality.UHD));

        var entity = mapper.toEntity(req);

        assertEquals(5400, entity.getDurationSeconds());
        assertTrue(entity.getSupportedQualities().contains(VideoQuality.HD));
        assertTrue(entity.getSupportedQualities().contains(VideoQuality.UHD));
    }
}


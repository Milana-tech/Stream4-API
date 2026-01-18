package com.stream.four.mapper;

import com.stream.four.dto.requests.CreateEpisodeRequest;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EpisodeMapperTest {

    private final EpisodeMapper mapper = Mappers.getMapper(EpisodeMapper.class);

    @Test
    void toEntity_mapsBasicFields() {
        var req = new CreateEpisodeRequest();
        req.setTitle("E1");

        var entity = mapper.toEntity(req);

        assertNotNull(entity);
        assertEquals("E1", entity.getName());
    }
}


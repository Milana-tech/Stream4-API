package com.stream.four.mapper;

import com.stream.four.dto.requests.CreateSeasonRequest;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SeasonMapperTest {

    private final SeasonMapper mapper = Mappers.getMapper(SeasonMapper.class);

    @Test
    void toEntity_mapsSeasonNumber() {
        var req = new CreateSeasonRequest();
        req.setSeasonNumber(1);

        var entity = mapper.toEntity(req);

        assertNotNull(entity);
        assertEquals(1, entity.getSeasonNumber());
    }
}

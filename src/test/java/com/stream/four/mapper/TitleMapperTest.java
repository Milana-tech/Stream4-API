package com.stream.four.mapper;

import com.stream.four.dto.CreateTitleRequest;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

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
}


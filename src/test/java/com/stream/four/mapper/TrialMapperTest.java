package com.stream.four.mapper;

import com.stream.four.model.subscription.Trial;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class TrialMapperTest {

    private final TrialMapper mapper = Mappers.getMapper(TrialMapper.class);

    @Test
    void toDto_mapsBasicFields() {
        var trial = new Trial();
        trial.setId("id1");
        trial.setStartDate(1L);
        trial.setEndDate(2L);
        trial.setUsed(true);

        var dto = mapper.toDto(trial);

        assertNotNull(dto);
        assertEquals("id1", dto.getId());
        assertEquals(1L, dto.getStartDate());
        assertEquals(2L, dto.getEndDate());
        assertTrue(dto.isUsed());
    }
}

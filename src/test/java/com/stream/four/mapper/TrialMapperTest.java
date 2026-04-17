package com.stream.four.mapper;

import com.stream.four.model.enums.TrialStatus;
import com.stream.four.model.subscription.Trial;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TrialMapperTest {

    private final TrialMapper mapper = Mappers.getMapper(TrialMapper.class);

    @Test
    void toDto_mapsBasicFields() {
        var start = LocalDate.of(2025, 1, 1);
        var end = LocalDate.of(2025, 1, 8);

        var trial = new Trial();
        trial.setStartDate(start);
        trial.setEndDate(end);
        trial.setStatus(TrialStatus.ACTIVE);
        trial.setConvertedToPaid(false);

        var dto = mapper.toDto(trial);

        assertNotNull(dto);
        assertEquals(start, dto.getStartDate());
        assertEquals(end, dto.getEndDate());
        assertFalse(dto.getConvertedToPaid());
    }
}

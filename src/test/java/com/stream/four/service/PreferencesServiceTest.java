package com.stream.four.service;

import com.stream.four.dto.response.watch.PreferencesResponse;
import com.stream.four.dto.update.UpdatePreferencesRequest;
import com.stream.four.mapper.PreferencesMapper;
import com.stream.four.model.watch.Preferences;
import com.stream.four.repository.PreferencesRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PreferencesServiceTest {

    private final PreferencesRepository preferencesRepository = mock(PreferencesRepository.class);
    private final PreferencesMapper preferencesMapper = mock(PreferencesMapper.class);

    private final PreferencesService preferencesService = new PreferencesService(preferencesRepository, preferencesMapper);

    @Test
    void getPreferences_missing_throws() {
        when(preferencesRepository.findByUserId("u")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> preferencesService.getPreferences("u"));
    }

    @Test
    void updatePreferences_whenNoneExists_createsSetsUserId_savesAndReturnsDto() {
        var req = new UpdatePreferencesRequest();
        var dto = new PreferencesResponse();

        when(preferencesRepository.findByUserId("u")).thenReturn(Optional.empty());
        when(preferencesMapper.toDto(any(Preferences.class))).thenReturn(dto);

        var result = preferencesService.updatePreferences("u", req);

        assertSame(dto, result);
        verify(preferencesMapper).updateEntity(eq(req), any(Preferences.class));
        verify(preferencesRepository).save(any(Preferences.class));
    }

    @Test
    void filterFilters_returnsFixedList() {
        assertEquals(3, preferencesService.filterFilters().size());
        assertTrue(preferencesService.filterFilters().contains("language"));
    }
}


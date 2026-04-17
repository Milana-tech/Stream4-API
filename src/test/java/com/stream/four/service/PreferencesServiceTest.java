package com.stream.four.service;

import com.stream.four.dto.response.watch.PreferencesResponse;
import com.stream.four.dto.update.UpdatePreferencesRequest;
import com.stream.four.mapper.PreferencesMapper;
import com.stream.four.model.enums.Genre;
import com.stream.four.model.enums.TitleType;
import com.stream.four.model.watch.Preferences;
import com.stream.four.repository.PreferencesRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PreferencesServiceTest {

    private final PreferencesRepository preferencesRepository = mock(PreferencesRepository.class);
    private final PreferencesMapper preferencesMapper = mock(PreferencesMapper.class);

    private final PreferencesService preferencesService = new PreferencesService(preferencesRepository, preferencesMapper);

    @Test
    void getPreferences_missing_throws() {
        when(preferencesRepository.findByProfileId("p1")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> preferencesService.getPreferences("p1"));
    }

    @Test
    void getPreferences_existing_returnsDto() {
        var prefs = new Preferences();
        var dto = new PreferencesResponse();

        when(preferencesRepository.findByProfileId("p1")).thenReturn(Optional.of(prefs));
        when(preferencesMapper.toDto(prefs)).thenReturn(dto);

        assertSame(dto, preferencesService.getPreferences("p1"));
    }

    @Test
    void updatePreferences_whenNoneExists_createsNew_setsProfileId_savesAndReturnsDto() {
        var req = new UpdatePreferencesRequest();
        req.setPreferredGenres(Set.of(Genre.ACTION));
        req.setPreferredType(TitleType.MOVIE);
        var dto = new PreferencesResponse();

        when(preferencesRepository.findByProfileId("p1")).thenReturn(Optional.empty());
        when(preferencesMapper.toDto(any(Preferences.class))).thenReturn(dto);

        var result = preferencesService.updatePreferences("p1", req);

        assertSame(dto, result);
        verify(preferencesMapper).updateEntity(eq(req), any(Preferences.class));
        verify(preferencesRepository).save(argThat(p -> "p1".equals(p.getProfileId())));
    }

    @Test
    void updatePreferences_whenExists_updatesExisting() {
        var existing = new Preferences();
        existing.setProfileId("p1");
        var req = new UpdatePreferencesRequest();
        var dto = new PreferencesResponse();

        when(preferencesRepository.findByProfileId("p1")).thenReturn(Optional.of(existing));
        when(preferencesMapper.toDto(existing)).thenReturn(dto);

        var result = preferencesService.updatePreferences("p1", req);

        assertSame(dto, result);
        verify(preferencesRepository).save(existing);
    }
}

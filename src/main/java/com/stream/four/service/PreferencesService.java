package com.stream.four.service;

import com.stream.four.dto.PreferencesResponse;
import com.stream.four.dto.UpdatePreferencesRequest;
import com.stream.four.mapper.PreferencesMapper;
import com.stream.four.model.Preferences;
import com.stream.four.repository.PreferencesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PreferencesService {

    private final PreferencesRepository preferencesRepository;
    private final PreferencesMapper preferencesMapper;

    public PreferencesResponse getPreferences(String userId) {
        var prefs = preferencesRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Preferences not found"));
        return preferencesMapper.toDto(prefs);
    }

    public PreferencesResponse updatePreferences(String userId, UpdatePreferencesRequest request) {
        var prefs = preferencesRepository.findByUserId(userId)
                .orElse(new Preferences());

        prefs.setUserId(userId);
        preferencesMapper.updateEntity(request, prefs);

        preferencesRepository.save(prefs);
        return preferencesMapper.toDto(prefs);
    }

    public List<String> filterFilters() {
        return List.of("language", "maturityLevel", "genres");
    }
}


package com.stream.four.service;

import com.stream.four.dto.response.watch.PreferencesResponse;
import com.stream.four.dto.update.UpdatePreferencesRequest;
import com.stream.four.exception.ResourceNotFoundException;
import com.stream.four.mapper.PreferencesMapper;
import com.stream.four.model.watch.Preferences;
import com.stream.four.repository.PreferencesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PreferencesService {

    private final PreferencesRepository preferencesRepository;
    private final PreferencesMapper preferencesMapper;

    public PreferencesResponse getPreferences(String profileId) {
        var prefs = preferencesRepository.findByProfileId(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Preferences not found for profile"));
        return preferencesMapper.toDto(prefs);
    }

    public PreferencesResponse updatePreferences(String profileId, UpdatePreferencesRequest request) {
        var prefs = preferencesRepository.findByProfileId(profileId)
                .orElse(new Preferences());

        prefs.setProfileId(profileId);
        preferencesMapper.updateEntity(request, prefs);

        preferencesRepository.save(prefs);
        return preferencesMapper.toDto(prefs);
    }
}

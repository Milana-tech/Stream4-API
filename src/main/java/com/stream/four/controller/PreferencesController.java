package com.stream.four.controller;

import com.stream.four.dto.PreferencesResponse;
import com.stream.four.dto.UpdatePreferencesRequest;
import com.stream.four.mapper.PreferencesMapper;
import com.stream.four.model.Preferences;
import com.stream.four.repository.PreferencesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/preferences")
public class PreferencesController {

    private final PreferencesRepository preferencesRepository;
    private final PreferencesMapper preferencesMapper;

    @GetMapping
    public PreferencesResponse getPreferences(Principal principal) {
        var prefs = preferencesRepository.findByUserId(principal.getName())
                .orElseThrow(() -> new RuntimeException("Preferences not found"));
                
        return preferencesMapper.toDto(prefs);
    }

    @PutMapping
    public PreferencesResponse updatePreferences(@RequestBody UpdatePreferencesRequest request, Principal principal) {

        var prefs = preferencesRepository.findByUserId(principal.getName())
                .orElse(new Preferences());

        prefs.setUserId(principal.getName());
        preferencesMapper.updateEntity(request, prefs);

        preferencesRepository.save(prefs);

        return preferencesMapper.toDto(prefs);
    }

    @GetMapping("/filterFilters")
    public List<String> filterFilters() {
        return List.of("language", "maturityLevel", "genres");
    }
}

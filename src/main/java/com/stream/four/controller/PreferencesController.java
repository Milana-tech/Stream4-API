package com.stream.four.controller;

import com.stream.four.dto.PreferencesResponse;
import com.stream.four.dto.UpdatePreferencesRequest;
import com.stream.four.service.PreferencesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/preferences")
public class PreferencesController {

    private final PreferencesService preferencesService;

    @GetMapping
    public PreferencesResponse getPreferences(Principal principal) {
        return preferencesService.getPreferences(principal.getName());
    }

    @PutMapping
    public PreferencesResponse updatePreferences(@Valid @RequestBody UpdatePreferencesRequest request, Principal principal) {
        return preferencesService.updatePreferences(principal.getName(), request);
    }

    @GetMapping("/filterFilters")
    public List<String> filterFilters() {
        return preferencesService.filterFilters();
    }
}

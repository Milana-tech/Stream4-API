package com.stream.four.controller.watch;

import com.stream.four.dto.response.watch.PreferencesResponse;
import com.stream.four.dto.update.UpdatePreferencesRequest;
import com.stream.four.service.PreferencesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping ("/preferences")
@Tag (name = "Preferences", description = "Manage user viewing preferences (supports JSON, XML, CSV)")
public class PreferencesController
{

    private final PreferencesService preferencesService;

    @GetMapping (produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation (summary = "Get preferences", description = "Get user's viewing preferences. Supports JSON, XML, CSV.")
    public PreferencesResponse getPreferences(Principal principal)
    {
        return preferencesService.getPreferences(principal.getName());
    }

    @PutMapping (produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation (summary = "Update preferences", description = "Update user's viewing preferences. Supports JSON, XML," +
            " CSV.")
    public PreferencesResponse updatePreferences(@Valid @RequestBody UpdatePreferencesRequest request,
                                                 Principal principal)
    {
        return preferencesService.updatePreferences(principal.getName(), request);
    }

    @GetMapping (value = "/filterFilters", produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation (summary = "Get available filters", description = "Get list of available content filters. Supports " +
            "JSON, XML, CSV.")
    public List<String> filterFilters()
    {
        return preferencesService.filterFilters();
    }
}
package com.stream.four.controller.watch;

import com.stream.four.dto.response.watch.PreferencesResponse;
import com.stream.four.dto.update.UpdatePreferencesRequest;
import com.stream.four.service.PreferencesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profiles/{profileId}/preferences")
@Tag(name = "preferences", description = "Manage profile viewing preferences (supports JSON, XML, CSV)")
public class PreferencesController {

    private final PreferencesService preferencesService;

    @GetMapping(produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation(summary = "Get preferences", description = "Get viewing preferences for a profile. Supports JSON, XML, CSV.")
    public ResponseEntity<PreferencesResponse> getPreferences(@PathVariable String profileId) {
        return ResponseEntity.ok(preferencesService.getPreferences(profileId));
    }

    @PutMapping(produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation(summary = "Update preferences", description = "Update viewing preferences for a profile. Supports JSON, XML, CSV.")
    public ResponseEntity<PreferencesResponse> updatePreferences(@PathVariable String profileId,
                                                                  @Valid @RequestBody UpdatePreferencesRequest request) {
        return ResponseEntity.ok(preferencesService.updatePreferences(profileId, request));
    }
}

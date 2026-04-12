package com.stream.four.controller.user;

import com.stream.four.dto.requests.CreateProfileRequest;
import com.stream.four.dto.response.user.ProfileResponse;
import com.stream.four.dto.update.UpdateProfileRequest;
import com.stream.four.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profiles")
@Tag(name = "Profiles", description = "Endpoints to manage user profiles (supports JSON, XML, CSV)")
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping(produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation(summary = "Create profile", description = "Create a new profile for the authenticated user")
    public ResponseEntity<ProfileResponse> createProfile(@Valid @RequestBody CreateProfileRequest request, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(profileService.createProfile(principal.getName(), request));
    }

    @GetMapping(produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @Operation(summary = "Get profiles", description = "Get all profiles for the authenticated user. Supports JSON, XML, CSV.")
    public ResponseEntity<List<ProfileResponse>> getProfiles(Principal principal) {
        return ResponseEntity.ok(profileService.getProfiles(principal.getName()));
    }

    @GetMapping(value = "/{id}", produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @PreAuthorize("@profileSecurity.canAccessProfile(#id, authentication.name)")
    @Operation(summary = "Get profile by ID", description = "Get specific profile. Supports JSON, XML, CSV.")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable String id) {
        return ResponseEntity.ok(profileService.getProfile(id));
    }

    @PutMapping(value = "/{id}", produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"
    })
    @PreAuthorize("@profileSecurity.canAccessProfile(#id, authentication.name)")
    @Operation(summary = "Update profile", description = "Update a profile")
    public ResponseEntity<ProfileResponse> updateProfile(@PathVariable String id, @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(profileService.updateProfile(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@profileSecurity.canAccessProfile(#id, authentication.name)")
    @Operation(summary = "Delete profile", description = "Delete a profile")
    public ResponseEntity<Void> deleteProfile(@PathVariable String id) {
        profileService.deleteProfile(id);
        return ResponseEntity.noContent().build();
    }
}
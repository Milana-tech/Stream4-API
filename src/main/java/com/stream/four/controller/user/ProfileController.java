package com.stream.four.controller.user;

import com.stream.four.dto.requests.CreateProfileRequest;
import com.stream.four.dto.response.user.ProfileResponse;
import com.stream.four.dto.update.UpdateProfileRequest;
import com.stream.four.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profiles")
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping
    public ProfileResponse createProfile(@Valid @RequestBody CreateProfileRequest request, Principal principal)
    {
        return profileService.createProfile(principal.getName(), request);
    }

    @GetMapping
    public List<ProfileResponse> getProfiles(Principal principal)
    {
        return profileService.getProfiles(principal.getName());
    }

    @GetMapping("/{id}")
    @PreAuthorize("@profileSecurity.canAccessProfile(#id, authentication.name)")
    public ProfileResponse getProfile(@PathVariable String id) {
        return profileService.getProfile(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@profileSecurity.canAccessProfile(#id, authentication.name)")
    public ProfileResponse updateProfile(@PathVariable String id, @Valid @RequestBody UpdateProfileRequest request) {
        return profileService.updateProfile(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@profileSecurity.canAccessProfile(#id, authentication.name)")
    public void deleteProfile(@PathVariable String id) {
        profileService.deleteProfile(id);
    }
}

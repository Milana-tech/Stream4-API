package com.stream.four.controller;

import com.stream.four.dto.*;
import com.stream.four.mapper.ProfileMapper;
import com.stream.four.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profiles")
public class ProfileController {

    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;

    @PostMapping
    public ProfileResponse createProfile(@RequestBody CreateProfileRequest request, Principal principal) 
    {
        var profile = profileMapper.toEntity(request);
        profile.setUserId(principal.getName());
        profileRepository.save(profile);

        return profileMapper.toDto(profile);
    }

    @GetMapping
    public List<ProfileResponse> getProfiles(Principal principal) 
    {
        return profileRepository.findByUserIdAndDeletedFalse(principal.getName()).stream().map(profileMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("@profileSecurity.canAccessProfile(#id, authentication.principal.username)")
    public ProfileResponse getProfile(@PathVariable String id) {
        var profile = profileRepository.findById(id).orElseThrow(() -> new RuntimeException("Profile not found"));

        return profileMapper.toDto(profile);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@profileSecurity.canAccessProfile(#id, authentication.principal.username)")
    public ProfileResponse updateProfile(@PathVariable String id, @RequestBody UpdateProfileRequest request) {
        var profile = profileRepository.findById(id).orElseThrow(() -> new RuntimeException("Profile not found"));

        profileMapper.updateEntity(request, profile);
        profileRepository.save(profile);

        return profileMapper.toDto(profile);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@profileSecurity.canAccessProfile(#id, authentication.principal.username)")
    public void deleteProfile(@PathVariable String id) {
        var profile = profileRepository.findById(id).orElseThrow(() -> new RuntimeException("Profile not found"));

        profile.setDeleted(true);
        profileRepository.save(profile);
    }
}

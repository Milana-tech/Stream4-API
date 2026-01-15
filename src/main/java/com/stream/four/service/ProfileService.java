package com.stream.four.service;

import com.stream.four.dto.*;
import com.stream.four.mapper.ProfileMapper;
import com.stream.four.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;

    public ProfileResponse createProfile(String userId, CreateProfileRequest request) {
        var profile = profileMapper.toEntity(request);
        profile.setUserId(userId);
        profileRepository.save(profile);
        return profileMapper.toDto(profile);
    }

    public List<ProfileResponse> getProfiles(String userId) {
        return profileRepository.findByUserIdAndDeletedFalse(userId)
                .stream()
                .map(profileMapper::toDto)
                .toList();
    }

    public ProfileResponse getProfile(String id) {
        var profile = profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        return profileMapper.toDto(profile);
    }

    public ProfileResponse updateProfile(String id, UpdateProfileRequest request) {
        var profile = profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        profileMapper.updateEntity(request, profile);
        profileRepository.save(profile);

        return profileMapper.toDto(profile);
    }

    public void deleteProfile(String id) {
        var profile = profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        profile.setDeleted(true);
        profileRepository.save(profile);
    }
}


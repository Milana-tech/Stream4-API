package com.stream.four.service;

import com.stream.four.dto.requests.CreateProfileRequest;
import com.stream.four.dto.response.user.ProfileResponse;
import com.stream.four.dto.update.UpdateProfileRequest;
import com.stream.four.exception.ResourceNotFoundException;
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

    private static final int MAX_PROFILES = 5;

    public ProfileResponse createProfile(String userId, CreateProfileRequest request) {
        long existing = profileRepository.findByUserIdAndDeletedFalse(userId).size();
        if (existing >= MAX_PROFILES) {
            throw new IllegalStateException("Maximum of " + MAX_PROFILES + " profiles allowed per account.");
        }

        var profile = profileMapper.toEntity(request);
        profile.setUserId(userId);
        profile.setMaturityLevel(deriveMaturityLevel(request.getAge()));
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
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        return profileMapper.toDto(profile);
    }

    public ProfileResponse updateProfile(String id, UpdateProfileRequest request) {
        var profile = profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        profileMapper.updateEntity(request, profile);
        profile.setMaturityLevel(deriveMaturityLevel(request.getAge()));
        profileRepository.save(profile);

        return profileMapper.toDto(profile);
    }

    private String deriveMaturityLevel(int age) {
        if (age < 12) return "KIDS";
        if (age < 18) return "TEENS";
        return "ADULT";
    }

    public void deleteProfile(String id) {
        var profile = profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        profile.setDeleted(true);
        profileRepository.save(profile);
    }
}


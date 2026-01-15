package com.stream.four.service;

import com.stream.four.dto.CreateProfileRequest;
import com.stream.four.dto.ProfileResponse;
import com.stream.four.dto.UpdateProfileRequest;
import com.stream.four.mapper.ProfileMapper;
import com.stream.four.model.Profile;
import com.stream.four.repository.ProfileRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileServiceTest {

    private final ProfileRepository profileRepository = mock(ProfileRepository.class);
    private final ProfileMapper profileMapper = mock(ProfileMapper.class);

    private final ProfileService profileService = new ProfileService(profileRepository, profileMapper);

    @Test
    void createProfile_setsUserId_savesAndReturnsDto() {
        var req = new CreateProfileRequest();
        req.setName("Kids");

        var entity = new Profile();
        var dto = new ProfileResponse();

        when(profileMapper.toEntity(req)).thenReturn(entity);
        when(profileMapper.toDto(entity)).thenReturn(dto);

        var result = profileService.createProfile("u1", req);

        assertSame(dto, result);
        assertEquals("u1", entity.getUserId());
        verify(profileRepository).save(entity);
    }

    @Test
    void getProfiles_mapsToDtos() {
        var p1 = new Profile();
        var p2 = new Profile();

        when(profileRepository.findByUserIdAndDeletedFalse("u")).thenReturn(List.of(p1, p2));
        when(profileMapper.toDto(p1)).thenReturn(new ProfileResponse());
        when(profileMapper.toDto(p2)).thenReturn(new ProfileResponse());

        var result = profileService.getProfiles("u");

        assertEquals(2, result.size());
        verify(profileRepository).findByUserIdAndDeletedFalse("u");
    }

    @Test
    void updateProfile_missing_throws() {
        when(profileRepository.findById("id")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> profileService.updateProfile("id", new UpdateProfileRequest()));
    }

    @Test
    void deleteProfile_marksDeletedAndSaves() {
        var profile = new Profile();
        profile.setDeleted(false);

        when(profileRepository.findById("id")).thenReturn(Optional.of(profile));

        profileService.deleteProfile("id");

        assertTrue(profile.isDeleted());
        verify(profileRepository).save(profile);
    }
}

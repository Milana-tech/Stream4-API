package com.stream.four.security;

import com.stream.four.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("profileSecurity")
@RequiredArgsConstructor
public class ProfileSecurity {

    private final ProfileRepository profileRepository;

    public boolean canAccessProfile(String profileId, String userId) {
        return profileRepository.existsByIdAndUserId(profileId, userId);
    }
}

package com.stream.four.service;

import com.stream.four.dto.requests.CreateTitleRequest;
import com.stream.four.dto.response.watch.TitleResponse;
import com.stream.four.exception.ResourceNotFoundException;
import com.stream.four.exception.UnauthorizedException;
import com.stream.four.mapper.TitleMapper;
import com.stream.four.repository.PreferencesRepository;
import com.stream.four.repository.ProfileRepository;
import com.stream.four.repository.TitleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TitleService {

    private final TitleRepository titleRepository;
    private final TitleMapper titleMapper;
    private final ProfileRepository profileRepository;
    private final PreferencesRepository preferencesRepository;
    private final ContentService contentService;

    public TitleResponse createTitle(CreateTitleRequest request) {
        var title = titleMapper.toEntity(request);
        titleRepository.save(title);
        return titleMapper.toDto(title);
    }

    public List<TitleResponse> getAllTitles() {
        return titleRepository.findByDeletedFalse()
                .stream()
                .map(titleMapper::toDto)
                .toList();
    }

    public TitleResponse getTitleById(String id) {
        var title = titleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Title not found"));
        return titleMapper.toDto(title);
    }

    public List<TitleResponse> getTitlesForProfile(String profileId) {
        var profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!currentUserId.equals(profile.getUserId())) {
            throw new UnauthorizedException("Access denied: profile does not belong to the current user");
        }

        var preferences = preferencesRepository.findByProfileId(profileId).orElse(null);
        var allTitles = titleRepository.findByDeletedFalse();

        return contentService.filterForProfile(allTitles, profile, preferences)
                .stream()
                .map(titleMapper::toDto)
                .toList();
    }
}


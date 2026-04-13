package com.stream.four.controller.watch;

import com.stream.four.service.PlaybackService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/playback")
@RequiredArgsConstructor
@Validated
public class PlaybackController {

    private final PlaybackService playbackService;

    @GetMapping("/test-playback")
    public ResponseEntity<String> testPlayback(
            @RequestParam @Email(message = "Must be a valid email address") @NotBlank(message = "Email is required") String email,
            @RequestParam @NotBlank(message = "Title name is required") String titleName,
            @RequestParam @NotBlank(message = "Profile ID is required") String profileId) {

        return ResponseEntity.ok(playbackService.getPlaybackQuality(email, titleName, profileId));
    }
}
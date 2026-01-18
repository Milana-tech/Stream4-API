package com.stream.four.controller.watch;
import com.stream.four.service.PlaybackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class PlaybackController
{
    private final PlaybackService playbackService;

    @GetMapping("/test-playback")
    public String testPlayback(
            @RequestParam String email,
            @RequestParam String titleName) {

        return playbackService.getPlaybackQuality(email, titleName);
    }
}

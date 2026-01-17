package com.stream.four.controller.watch;

import com.stream.four.dto.requests.CreateWatchEventRequest;
import com.stream.four.dto.response.watch.WatchEventResponse;
import com.stream.four.service.ViewingBehaviourService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class ViewingBehaviourController {

    private final ViewingBehaviourService viewingBehaviourService;

    @PostMapping("/watch")
    public WatchEventResponse watch(@Valid @RequestBody CreateWatchEventRequest request, Principal principal) {
        return viewingBehaviourService.watch(principal.getName(), request);
    }

    @GetMapping("/history")
    public List<WatchEventResponse> history(Principal principal) {
        return viewingBehaviourService.history(principal.getName());
    }

    @GetMapping("/progress/{titleId}")
    public WatchEventResponse progress(@PathVariable String titleId, Principal principal) {
        return viewingBehaviourService.progress(principal.getName(), titleId);
    }
}

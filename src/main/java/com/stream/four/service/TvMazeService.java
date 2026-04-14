package com.stream.four.service;

import com.stream.four.dto.response.watch.TvMazeShowResponse;
import com.stream.four.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TvMazeService {

    private static final String TVMAZE_SEARCH_URL = "https://api.tvmaze.com/search/shows?q={query}";
    private static final String TVMAZE_SINGLE_URL  = "https://api.tvmaze.com/singlesearch/shows?q={query}";

    private final RestTemplate restTemplate;

    public List<TvMazeShowResponse> searchShows(String query) {
        try {
            TvMazeSearchResult[] results = restTemplate.getForObject(
                    TVMAZE_SEARCH_URL, TvMazeSearchResult[].class, query);

            if (results == null || results.length == 0) {
                return List.of();
            }

            return Arrays.stream(results)
                    .map(TvMazeSearchResult::getShow)
                    .toList();

        } catch (HttpClientErrorException e) {
            log.warn("TVmaze search failed for query '{}': {}", query, e.getMessage());
            return List.of();
        }
    }

    public TvMazeShowResponse getShow(String query) {
        try {
            return restTemplate.getForObject(TVMAZE_SINGLE_URL, TvMazeShowResponse.class, query);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("No show found on TVmaze for: " + query);
        } catch (HttpClientErrorException e) {
            log.warn("TVmaze lookup failed for query '{}': {}", query, e.getMessage());
            throw new ResourceNotFoundException("TVmaze lookup failed: " + e.getMessage());
        }
    }

    // Internal wrapper matching TVmaze search response structure
    @lombok.Data
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    static class TvMazeSearchResult {
        private Double score;
        private TvMazeShowResponse show;
    }
}

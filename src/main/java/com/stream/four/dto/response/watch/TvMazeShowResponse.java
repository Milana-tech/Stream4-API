package com.stream.four.dto.response.watch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TvMazeShowResponse {

    private Integer id;
    private String name;
    private String status;
    private String type;
    private String language;

    @JsonProperty("premiered")
    private String premiered;

    @JsonProperty("officialSite")
    private String officialSite;

    private RatingInfo rating;
    private NetworkInfo network;
    private ImageInfo image;
    private String summary;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RatingInfo {
        private Double average;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NetworkInfo {
        private String name;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ImageInfo {
        private String medium;
        private String original;
    }
}

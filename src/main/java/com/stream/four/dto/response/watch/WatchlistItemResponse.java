package com.stream.four.dto.response.watch;

import lombok.Data;

@Data
public class WatchlistItemResponse {
    private String id;

    private String titleId;
    
    private long addedAt;
}

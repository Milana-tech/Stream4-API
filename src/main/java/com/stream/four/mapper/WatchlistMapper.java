package com.stream.four.mapper;

import com.stream.four.dto.requests.CreateWatchlistItemRequest;
import com.stream.four.dto.response.watch.WatchlistItemResponse;
import com.stream.four.model.watch.WatchlistItem;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WatchlistMapper {

    WatchlistItem toEntity(CreateWatchlistItemRequest request);

    WatchlistItemResponse toDto(WatchlistItem item);
}

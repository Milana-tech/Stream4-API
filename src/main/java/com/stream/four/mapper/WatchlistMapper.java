package com.stream.four.mapper;

import com.stream.four.dto.*;
import com.stream.four.model.WatchlistItem;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WatchlistMapper {

    WatchlistItem toEntity(CreateWatchlistItemRequest request);

    WatchlistItemResponse toDto(WatchlistItem item);
}

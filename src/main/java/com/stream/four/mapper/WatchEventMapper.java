package com.stream.four.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.stream.four.dto.requests.CreateWatchEventRequest;
import com.stream.four.dto.response.watch.WatchEventResponse;
import com.stream.four.model.watch.WatchEvent;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WatchEventMapper {

    WatchEvent toEntity(CreateWatchEventRequest request);

    WatchEventResponse toDto(WatchEvent event);
}

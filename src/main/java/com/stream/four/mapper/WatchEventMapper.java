package com.stream.four.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.stream.four.dto.CreateWatchEventRequest;
import com.stream.four.dto.WatchEventResponse;
import com.stream.four.model.WatchEvent;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WatchEventMapper {

    WatchEvent toEntity(CreateWatchEventRequest request);

    WatchEventResponse toDto(WatchEvent event);
}

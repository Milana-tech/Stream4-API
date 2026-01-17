package com.stream.four.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.stream.four.dto.requests.CreateSeasonRequest;
import com.stream.four.dto.response.watch.SeasonResponse;
import com.stream.four.model.watch.Season;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SeasonMapper {
    Season toEntity(CreateSeasonRequest request);

    SeasonResponse toDto(Season season);
}

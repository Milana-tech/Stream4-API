package com.stream.four.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.stream.four.dto.CreateSeasonRequest;
import com.stream.four.dto.SeasonResponse;
import com.stream.four.model.Season;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SeasonMapper {
    Season toEntity(CreateSeasonRequest request);

    SeasonResponse toDto(Season season);
}

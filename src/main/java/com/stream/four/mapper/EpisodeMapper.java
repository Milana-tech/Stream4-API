package com.stream.four.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.stream.four.dto.CreateEpisodeRequest;
import com.stream.four.dto.EpisodeResponse;
import com.stream.four.model.Episode;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EpisodeMapper {
    Episode toEntity(CreateEpisodeRequest request);

    EpisodeResponse toDto(Episode episode);
}

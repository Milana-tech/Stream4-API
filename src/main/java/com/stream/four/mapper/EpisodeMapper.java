package com.stream.four.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.stream.four.dto.requests.CreateEpisodeRequest;
import com.stream.four.dto.response.watch.EpisodeResponse;
import com.stream.four.model.watch.Episode;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EpisodeMapper {
    Episode toEntity(CreateEpisodeRequest request);

    EpisodeResponse toDto(Episode episode);
}

package com.stream.four.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.stream.four.dto.requests.CreateEpisodeRequest;
import com.stream.four.dto.response.watch.EpisodeResponse;
import com.stream.four.model.watch.Episode;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EpisodeMapper {
    @Mapping(source = "title", target = "name")
    Episode toEntity(CreateEpisodeRequest request);

    EpisodeResponse toDto(Episode episode);
}

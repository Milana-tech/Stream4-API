package com.stream.four.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.stream.four.dto.requests.CreateTitleRequest;
import com.stream.four.dto.response.watch.TitleResponse;
import com.stream.four.model.watch.Title;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TitleMapper {
    Title toEntity(CreateTitleRequest request);

    TitleResponse toDto(Title title);
}

package com.stream.four.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.stream.four.dto.CreateTitleRequest;
import com.stream.four.dto.TitleResponse;
import com.stream.four.model.Title;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TitleMapper {
    Title toEntity(CreateTitleRequest request);

    TitleResponse toDto(Title title);
}

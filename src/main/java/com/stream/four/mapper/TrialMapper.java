package com.stream.four.mapper;

import com.stream.four.dto.response.subscription.TrialResponse;
import com.stream.four.model.subscription.Trial;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TrialMapper {

    TrialResponse toDto(Trial trial);
}

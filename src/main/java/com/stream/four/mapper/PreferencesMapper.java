package com.stream.four.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.stream.four.dto.PreferencesResponse;
import com.stream.four.dto.UpdatePreferencesRequest;
import com.stream.four.model.Preferences;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PreferencesMapper {

    PreferencesResponse toDto(Preferences preferences);

    void updateEntity(UpdatePreferencesRequest request, @MappingTarget Preferences preferences);
}

package com.stream.four.mapper;

import com.stream.four.dto.*;
import com.stream.four.model.Profile;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProfileMapper {
    
    Profile toEntity(CreateProfileRequest request);

    ProfileResponse toDto(Profile profile);

    void updateEntity(UpdateProfileRequest request, @MappingTarget Profile profile);
}

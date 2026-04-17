package com.stream.four.mapper;

import com.stream.four.dto.requests.CreateProfileRequest;
import com.stream.four.dto.response.user.ProfileResponse;
import com.stream.four.dto.update.UpdateProfileRequest;
import com.stream.four.model.user.Profile;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProfileMapper {
    
    Profile toEntity(CreateProfileRequest request);

    ProfileResponse toDto(Profile profile);

    void updateEntity(UpdateProfileRequest request, @MappingTarget Profile profile);
}

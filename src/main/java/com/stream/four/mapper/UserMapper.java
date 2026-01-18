package com.stream.four.mapper;

import com.stream.four.dto.requests.CreateUserRequest;
import com.stream.four.dto.response.user.UserResponse;
import com.stream.four.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "failedLoginAttempts", constant = "0")
    @Mapping(target = "resetToken", ignore = true) // Explicitly ignore or let it be null
    User toEntity(CreateUserRequest request);

    UserResponse toDto(User user);

}
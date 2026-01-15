package com.stream.four.mapper;

import com.stream.four.dto.CreateUserRequest;
import com.stream.four.dto.UserResponse;
import com.stream.four.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface UserMapper
{

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "failedLoginAttempts", constant = "0")    //users start with 0 failed attempts
    @Mapping(target = "accountLocked", constant = "false")  //users start unlocked
    User toEntity(CreateUserRequest request);

    UserResponse toDto(User request);
}
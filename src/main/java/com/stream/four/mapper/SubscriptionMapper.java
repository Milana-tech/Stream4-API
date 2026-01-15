package com.stream.four.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.stream.four.dto.CreateSubscriptionRequest;
import com.stream.four.dto.SubscriptionResponse;
import com.stream.four.model.Subscription;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubscriptionMapper {
    Subscription toEntity(CreateSubscriptionRequest request);

    SubscriptionResponse toDto(Subscription subscription);
}

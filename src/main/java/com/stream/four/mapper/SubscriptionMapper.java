package com.stream.four.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.stream.four.dto.requests.CreateSubscriptionRequest;
import com.stream.four.dto.response.subscription.SubscriptionResponse;
import com.stream.four.model.subscription.Subscription;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubscriptionMapper {
    Subscription toEntity(CreateSubscriptionRequest request);

    SubscriptionResponse toDto(Subscription subscription);
}

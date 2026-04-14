package com.stream.four.mapper;

import com.stream.four.dto.requests.CreateSubscriptionRequest;
import com.stream.four.model.enums.SubscriptionPlan;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionMapperTest {

    private final SubscriptionMapper mapper = Mappers.getMapper(SubscriptionMapper.class);

    @Test
    void toEntity_mapsBasicFields() {
        var req = new CreateSubscriptionRequest();
        req.setPlan(SubscriptionPlan.HD);

        var entity = mapper.toEntity(req);

        assertNotNull(entity);
        assertEquals("HD", entity.getPlan());
    }
}

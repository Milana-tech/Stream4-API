package com.stream.four.mapper;

import com.stream.four.dto.CreateProfileRequest;
import com.stream.four.dto.UpdateProfileRequest;
import com.stream.four.model.Profile;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class ProfileMapperTest {

    private final ProfileMapper mapper = Mappers.getMapper(ProfileMapper.class);

    @Test
    void toEntity_mapsBasicFields() {
        var req = new CreateProfileRequest();
        req.setName("Kids");

        var entity = mapper.toEntity(req);

        assertNotNull(entity);
        assertEquals("Kids", entity.getName());
    }

    @Test
    void updateEntity_updatesTarget() {
        var profile = new Profile();
        profile.setName("Old");

        var update = new UpdateProfileRequest();
        update.setName("New");

        mapper.updateEntity(update, profile);

        assertEquals("New", profile.getName());
    }
}


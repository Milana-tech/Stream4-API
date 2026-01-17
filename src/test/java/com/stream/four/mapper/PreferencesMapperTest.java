package com.stream.four.mapper;

import com.stream.four.dto.update.UpdatePreferencesRequest;
import com.stream.four.model.watch.Preferences;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class PreferencesMapperTest {

    private final PreferencesMapper mapper = Mappers.getMapper(PreferencesMapper.class);

    @Test
    void updateEntity_updatesFields() {
        var prefs = new Preferences();
        prefs.setLanguage("en");

        var update = new UpdatePreferencesRequest();
        update.setLanguage("de");

        mapper.updateEntity(update, prefs);

        assertEquals("de", prefs.getLanguage());
    }
}


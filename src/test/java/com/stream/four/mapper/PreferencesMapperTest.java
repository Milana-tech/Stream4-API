package com.stream.four.mapper;

import com.stream.four.dto.update.UpdatePreferencesRequest;
import com.stream.four.model.enums.Genre;
import com.stream.four.model.enums.MaturityRating;
import com.stream.four.model.enums.TitleType;
import com.stream.four.model.watch.Preferences;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PreferencesMapperTest {

    private final PreferencesMapper mapper = Mappers.getMapper(PreferencesMapper.class);

    @Test
    void updateEntity_updatesAllFields() {
        var prefs = new Preferences();

        var update = new UpdatePreferencesRequest();
        update.setPreferredGenres(Set.of(Genre.ACTION, Genre.COMEDY));
        update.setPreferredType(TitleType.MOVIE);
        update.setMinimumMaturityRating(MaturityRating.TEEN);

        mapper.updateEntity(update, prefs);

        assertEquals(Set.of(Genre.ACTION, Genre.COMEDY), prefs.getPreferredGenres());
        assertEquals(TitleType.MOVIE, prefs.getPreferredType());
        assertEquals(MaturityRating.TEEN, prefs.getMinimumMaturityRating());
    }

    @Test
    void toDto_mapsAllFields() {
        var prefs = new Preferences();
        prefs.setProfileId("p1");
        prefs.setPreferredGenres(Set.of(Genre.DRAMA));
        prefs.setPreferredType(TitleType.SERIES);
        prefs.setMinimumMaturityRating(MaturityRating.PG);

        var dto = mapper.toDto(prefs);

        assertNotNull(dto);
        assertEquals("p1", dto.getProfileId());
        assertTrue(dto.getPreferredGenres().contains(Genre.DRAMA));
        assertEquals(TitleType.SERIES, dto.getPreferredType());
        assertEquals(MaturityRating.PG, dto.getMinimumMaturityRating());
    }
}

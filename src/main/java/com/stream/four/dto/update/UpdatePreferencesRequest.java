package com.stream.four.dto.update;

import com.stream.four.model.enums.Genre;
import com.stream.four.model.enums.MaturityRating;
import com.stream.four.model.enums.TitleType;
import lombok.Data;

import java.util.Set;

@Data
public class UpdatePreferencesRequest {

    private Set<Genre> preferredGenres;

    private TitleType preferredType;

    private MaturityRating minimumMaturityRating;
}

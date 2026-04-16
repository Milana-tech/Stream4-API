package com.stream.four.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {

    private final MaturityLevelValidator maturityValidator = new MaturityLevelValidator();
    private final UUIDValidator uuidValidator = new UUIDValidator();

    // --- MaturityLevelValidator ---

    @Test
    void maturity_validKids() {
        assertTrue(maturityValidator.isValid("KIDS", null));
    }

    @Test
    void maturity_validTeens() {
        assertTrue(maturityValidator.isValid("TEENS", null));
    }

    @Test
    void maturity_validAdult() {
        assertTrue(maturityValidator.isValid("ADULT", null));
    }

    @Test
    void maturity_caseInsensitive() {
        assertTrue(maturityValidator.isValid("kids", null));
        assertTrue(maturityValidator.isValid("Teens", null));
    }

    @Test
    void maturity_invalid() {
        assertFalse(maturityValidator.isValid("UNKNOWN", null));
    }

    @Test
    void maturity_null() {
        assertFalse(maturityValidator.isValid(null, null));
    }

    // --- UUIDValidator ---

    @Test
    void uuid_validUUID() {
        assertTrue(uuidValidator.isValid("550e8400-e29b-41d4-a716-446655440000", null));
    }

    @Test
    void uuid_nullIsValid() {
        assertTrue(uuidValidator.isValid(null, null));
    }

    @Test
    void uuid_blankIsValid() {
        assertTrue(uuidValidator.isValid("  ", null));
    }

    @Test
    void uuid_invalidFormat() {
        assertFalse(uuidValidator.isValid("not-a-uuid", null));
    }

    @Test
    void uuid_emptyStringIsValid() {
        assertTrue(uuidValidator.isValid("", null));
    }
}

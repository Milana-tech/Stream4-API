package com.stream.four.validation;

import java.util.Set;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MaturityLevelValidator implements ConstraintValidator<ValidMaturityLevel, String> {

    public static final Set<String> ALLOWED = Set.of("KIDS", "TEENS", "ADULT");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context)
    {
        if (value == null)
            return false;
        return ALLOWED.contains(value.toUpperCase());
    }
}

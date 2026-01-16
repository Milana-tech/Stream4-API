package com.stream.four.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MaturityLevelValidator.class)
public @interface ValidMaturityLevel {
    String message() default "Invalid maturity level";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

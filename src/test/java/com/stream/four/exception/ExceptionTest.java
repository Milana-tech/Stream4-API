package com.stream.four.exception;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionTest {

    @Test
    void resourceNotFoundException_storesMessage() {
        var ex = new ResourceNotFoundException("not found");
        assertEquals("not found", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    void duplicateResourceException_storesMessage() {
        var ex = new DuplicateResourceException("already exists");
        assertEquals("already exists", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    void unauthorizedException_storesMessage() {
        var ex = new UnauthorizedException("unauthorized");
        assertEquals("unauthorized", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    void errorResponse_gettersAndSetters() {
        var errors = Map.of("field", "must not be blank");
        var response = new ErrorResponse("Validation failed", errors);
        assertEquals("Validation failed", response.getMessage());
        assertEquals(errors, response.getErrors());
    }

    @Test
    void errorResponse_nullErrors() {
        var response = new ErrorResponse("Something went wrong", null);
        assertEquals("Something went wrong", response.getMessage());
        assertNull(response.getErrors());
    }
}

package com.stream.four.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleNotFound_returns404() {
        var ex = new ResourceNotFoundException("User not found");
        var response = handler.handleNotFound(ex);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody().getMessage());
    }

    @Test
    void handleDuplicate_returns409() {
        var ex = new DuplicateResourceException("Already exists");
        var response = handler.handleDuplicate(ex);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Already exists", response.getBody().getMessage());
    }

    @Test
    void handleUnauthorized_returns401() {
        var ex = new UnauthorizedException("Bad token");
        var response = handler.handleUnauthorized(ex);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Bad token", response.getBody().getMessage());
    }

    @Test
    void handleAccessDenied_returns403() {
        var ex = new AccessDeniedException("Forbidden");
        var response = handler.handleAccessDenied(ex);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Access denied", response.getBody().getMessage());
    }

    @Test
    void handleDataIntegrity_returns409() {
        var ex = new DataIntegrityViolationException("Duplicate key");
        var response = handler.handleDataIntegrity(ex);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody().getMessage());
    }

    @Test
    void handleIllegalArgument_returns400() {
        var ex = new IllegalArgumentException("Bad input");
        var response = handler.handleIllegalArgument(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad input", response.getBody().getMessage());
    }

    @Test
    void handleIllegalState_returns400() {
        var ex = new IllegalStateException("Invalid state");
        var response = handler.handleIllegalArgument(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid state", response.getBody().getMessage());
    }

    @Test
    void handleMissingParam_returns400() throws MissingServletRequestParameterException {
        var ex = new MissingServletRequestParameterException("email", "String");
        var response = handler.handleMissingParam(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("email"));
    }

    @Test
    void handleNotReadable_invalidBody_returns400() {
        var ex = new HttpMessageNotReadableException("Invalid request body",
                new MockHttpInputMessage(new byte[0]));
        var response = handler.handleNotReadable(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid request body", response.getBody().getMessage());
    }

    @Test
    void handleGeneric_returns500() {
        var ex = new RuntimeException("Unexpected");
        var response = handler.handleGeneric(ex);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
    }

    @Test
    void handleConstraintViolation_returns400() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("email");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must not be blank");

        var ex = new ConstraintViolationException(Set.of(violation));
        var response = handler.handleConstraintViolation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation failed", response.getBody().getMessage());
        assertTrue(response.getBody().getErrors().containsKey("email"));
    }
}

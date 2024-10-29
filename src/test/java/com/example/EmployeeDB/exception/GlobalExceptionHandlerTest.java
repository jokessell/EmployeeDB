package com.example.EmployeeDB.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleResourceNotFound() {
        ResponseEntity<String> response = handler.handleResourceNotFound(new ResourceNotFoundException("Resource not found"));
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
        assertEquals("Resource not found", response.getBody());
    }

//    @Test
//    void testHandleValidationExceptions() {
//        BindingResult bindingResult = mock(BindingResult.class);
//        List<FieldError> fieldErrors = List.of(new FieldError("objectName", "field", "error message"));
//        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
//
//        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
//
//        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex);
//
//        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
//        assertEquals("error message", response.getBody().get("field"));
//    }

    @Test
    void testHandleGenericException() {
        ResponseEntity<String> response = handler.handleGenericException(new Exception("Generic error"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCode().value());
        assertEquals("An unexpected error occurred.", response.getBody());
    }
}
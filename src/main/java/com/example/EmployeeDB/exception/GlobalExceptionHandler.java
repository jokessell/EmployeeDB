// src/main/java/com/example/EmployeeDB/exception/GlobalExceptionHandler.java

package com.example.EmployeeDB.exception;

import com.example.EmployeeDB.dto.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler handles various exceptions across the application,
 * providing consistent and meaningful responses to the client.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles ResourceNotFoundException and returns a 404 Not Found response.
     *
     * @param ex the ResourceNotFoundException
     * @return ResponseEntity with error message and status
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<MessageResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        MessageResponse response = new MessageResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Handles MethodArgumentNotValidException and returns a 400 Bad Request response
     * with detailed validation errors.
     *
     * @param ex the MethodArgumentNotValidException
     * @return ResponseEntity with field-specific error messages and status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Handles MethodArgumentTypeMismatchException and returns a 400 Bad Request response
     * with a specific error message.
     *
     * @param ex the MethodArgumentTypeMismatchException
     * @return ResponseEntity with error message and status
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<MessageResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Invalid value '%s' for parameter '%s'.", ex.getValue(), ex.getName());
        MessageResponse response = new MessageResponse(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handles UsernameNotFoundException and returns a 401 Unauthorized response.
     *
     * @param ex the UsernameNotFoundException
     * @return ResponseEntity with error message and status
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<MessageResponse> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        MessageResponse response = new MessageResponse("Error: User not found.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Handles generic exceptions and returns a 500 Internal Server Error response.
     *
     * @param ex the Exception
     * @return ResponseEntity with error message and status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponse> handleGenericException(Exception ex) {
        ex.printStackTrace(); // For debugging purposes; consider removing in production
        MessageResponse response = new MessageResponse("An unexpected error occurred.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

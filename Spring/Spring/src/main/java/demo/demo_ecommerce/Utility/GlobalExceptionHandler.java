package demo.demo_ecommerce.Utility;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Gestione delle ConstraintViolationException
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(ConstraintViolationException ex,
            WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Validation failed");
        response.put("details", ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage)));
        response.put("path", request.getDescription(false));

        logger.error("Validation error: {}", response);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Gestione delle MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        DefaultMessageSourceResolvable::getDefaultMessage));

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Validation failed");
        response.put("details", errors);

        logger.error("Validation error: {}", response);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Gestione di eccezioni generiche
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex, WebRequest request) {
        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);

        Map<String, String> response = new HashMap<>();
        response.put("error", "An unexpected error occurred");
        response.put("details", ex.getMessage());
        response.put("path", request.getDescription(false));

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }





        @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Resource not found");
        response.put("details", ex.getMessage());
        response.put("path", request.getDescription(false));

        logger.warn("Resource not found: {}", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, String>> handleConflictException(ConflictException ex, WebRequest request) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Conflict error");
        response.put("details", ex.getMessage());
        response.put("path", request.getDescription(false));

        logger.warn("Conflict: {}", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(org.springframework.dao.DataIntegrityViolationException ex, WebRequest request) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Database integrity violation");
        ex.getRootCause();
        response.put("details", ex.getRootCause().getMessage());
        response.put("path", request.getDescription(false));

        logger.error("Data integrity violation: {}", response);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDeniedException() {
        return "Accesso negato: non hai i permessi necessari.";


    }

}

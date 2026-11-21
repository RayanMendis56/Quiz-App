package com.rayan.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // @Valid failed on a request body
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.putIfAbsent(fe.getField(), fe.getDefaultMessage());
        }
        return build(HttpStatus.BAD_REQUEST, "Validation failed", fieldErrors);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    // Constraints on @RequestParam values (used in step 4)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiError> handleParamValidation(HandlerMethodValidationException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getParameterValidationResults().forEach(result -> {
            String name = result.getMethodParameter().getParameterName();
            result.getResolvableErrors().forEach(err ->
                    fieldErrors.putIfAbsent(name, err.getDefaultMessage()));
        });
        return build(HttpStatus.BAD_REQUEST, "Validation failed", fieldErrors);
    }

    // Broken JSON, or a value of the wrong type (e.g. text where a number belongs)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadable(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, "Malformed or unreadable request body", null);
    }

    // A required ?param= is missing from the URL
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParam(MissingServletRequestParameterException ex) {
        return build(HttpStatus.BAD_REQUEST,
                "Required parameter '" + ex.getParameterName() + "' is missing", null);
    }

    // A URL value of the wrong type (e.g. /quiz/get/abc)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return build(HttpStatus.BAD_REQUEST,
                "Parameter '" + ex.getName() + "' has an invalid value '" + ex.getValue() + "'", null);
    }

    // Safety net for everything else
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAny(Exception ex) {
        // Spring's own web errors (404 unknown URL, 405 wrong method) already carry the right status
        if (ex instanceof ErrorResponse springError) {
            HttpStatus status = HttpStatus.valueOf(springError.getStatusCode().value());
            String detail = springError.getBody().getDetail();
            return build(status, detail != null ? detail : status.getReasonPhrase(), null);
        }

        log.error("Unexpected error", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong on our side", null);
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String message, Map<String, String> fieldErrors) {
        ApiError body = new ApiError(LocalDateTime.now(), status.value(),
                status.getReasonPhrase(), message, fieldErrors);
        return new ResponseEntity<>(body, status);
    }
}
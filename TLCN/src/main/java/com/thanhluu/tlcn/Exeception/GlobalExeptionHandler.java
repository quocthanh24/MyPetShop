package com.thanhluu.tlcn.Exeception;

import com.thanhluu.tlcn.DTO.response.Error.ErrorResponse;
import com.thanhluu.tlcn.Enum.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;

import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExeptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(BadRequestException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
          .errorCode(ex.getErrorCode())
          .path(request.getRequestURI())
          .timestamp(LocalDateTime.now())
          .build();
        return ResponseEntity.status(errorResponse.getErrorCode().getHttpStatus())
          .body(errorResponse);
    }

    // Handle error when the client uses an unsupported HTTP method (e.g., GET instead of POST)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        String message = String.format("HTTP method '%s' is not supported for this endpoint. Please use: %s",
          ex.getMethod(), ex.getSupportedHttpMethods());
        log.error(message);
        ErrorResponse errorResponse = ErrorResponse.builder()
          .errorCode(ErrorCode.INVALID_HTTP)
          .path(request.getRequestURI())
          .timestamp(LocalDateTime.now())
          .build();
        return ResponseEntity.status(errorResponse.getErrorCode().getHttpStatus())
          .body(errorResponse);
    }

    // Handling errors when passing incorrect parameter types (e.g., wrong data type in '@PathVariable')
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String message = String.format(
          "The parameter '%s' has an invalid value '%s'. Expected format: '%s'.",
          ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName()
        );
        log.error(message);
        ErrorResponse errorResponse = ErrorResponse.builder()
          .errorCode(ErrorCode.INVALID_PARAMETER_TYPE)
          .path(request.getRequestURI())
          .timestamp(LocalDateTime.now())
          .build();
        return ResponseEntity.status(errorResponse.getErrorCode().getHttpStatus())
          .body(errorResponse);
    }

    // Handling errors when passing an invalid request body (e.g., malformed '@RequestBody')
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String message = ex.getMostSpecificCause().getMessage();
        log.error(message);
        ErrorResponse errorResponse = ErrorResponse.builder()
          .errorCode(ErrorCode.INVALID_REQUEST_BODY)
          .path(request.getRequestURI())
          .timestamp(LocalDateTime.now())
          .build();
        return ResponseEntity.status(errorResponse.getErrorCode().getHttpStatus())
          .body(errorResponse);
    }

    // Handling errors when required parameters are missing (e.g., missing '@RequestParam' or '@PathVariable')
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpServletRequest request) {
        String message = String.format(
          "Missing required parameter '%s' of type '%s'.",
          ex.getParameterName(), ex.getParameterType()
        );
        log.error(message);
        ErrorResponse errorResponse = ErrorResponse.builder()
          .errorCode(ErrorCode.MISSING_REQUIRED_PARAMETER)
          .path(request.getRequestURI())
          .timestamp(LocalDateTime.now())
          .build();
        return ResponseEntity.status(errorResponse.getErrorCode().getHttpStatus())
          .body(errorResponse);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        log.error("Media Type {} is not supported.", ex.getContentType());
        ErrorResponse errorResponse = ErrorResponse.builder()
          .errorCode(ErrorCode.UNSUPPORTED_MEDIA_TYPE)
          .path(request.getRequestURI())
          .timestamp(LocalDateTime.now())
          .build();
        return ResponseEntity.status(errorResponse.getErrorCode().getHttpStatus())
          .body(errorResponse);
    }

    // Handle validation errors for @RequestBody/@ModelAttribute fields
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
          errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

    // Handle validation errors for method parameters (@RequestParam, @PathVariable, @RequestPart)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
        log.error("Validation constraint violation: ", ex);
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(violation ->
          errors.put(violation.getPropertyPath().toString(), violation.getMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("error", "Bad Request");
        response.put("message", ex.getMessage());
        response.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handles exception when a requested resource is not found (e.g., search result missing)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Object> handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest request) {
        log.error("Resource not found exception.", ex);
        ErrorResponse errorResponse = ErrorResponse.builder()
          .errorCode(ErrorCode.RESOURCE_NOT_FOUND)
          .path(request.getRequestURI())
          .timestamp(LocalDateTime.now())
          .build();
        return ResponseEntity.status(errorResponse.getErrorCode().getHttpStatus())
          .body(errorResponse);
    }

    @ExceptionHandler({ AccessDeniedException.class, AuthorizationDeniedException.class })
    public ResponseEntity<ErrorResponse> handleAccessDenied(Exception ex, HttpServletRequest request) {
        log.error("Access denied exception.", ex);
        ErrorResponse errorResponse = ErrorResponse.builder()
          .errorCode(ErrorCode.ACCESS_DENIED)
          .path(request.getRequestURI())
          .timestamp(LocalDateTime.now())
          .build();
        return ResponseEntity.status(errorResponse.getErrorCode().getHttpStatus())
          .body(errorResponse);
    }

    // Handling other unknown errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOtherExceptions(Exception ex, HttpServletRequest request) {
        log.error("Exception: ", ex);
        ErrorResponse errorResponse = ErrorResponse.builder()
          .errorCode(ErrorCode.OTHER_EXCEPTIONS)
          .path(request.getRequestURI())
          .timestamp(LocalDateTime.now())
          .build();
        return ResponseEntity
          .status(errorResponse.getErrorCode().getHttpStatus())
          .body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("error", "Internal Server Error");
        response.put("message", ex.getMessage());
        response.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

package com.moocode.apifacturas.handler;

import com.moocode.apifacturas.dto.response.ApiErrorResponse;
import com.moocode.apifacturas.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.Instant;

public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity <ApiErrorResponse>  notFound(ResourceNotFoundException exception, HttpServletRequest request){
        return error(HttpStatus.CONFLICT, "RESOURCE-409", exception.getMessage(),request);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    ResponseEntity <ApiErrorResponse>  duplicate(DuplicateResourceException exception, HttpServletRequest request){
        return error(HttpStatus.CONFLICT, "RESOURCE-409", exception.getMessage(), request);
    }

    @ExceptionHandler(InvalidCredentialException.class)
    ResponseEntity<ApiErrorResponse> invalidCredentials(InvalidCredentialException exception,
                                                        HttpServletRequest request) {
        return error(HttpStatus.UNAUTHORIZED, "AUTH-401", exception.getMessage(), request);
    }

    @ExceptionHandler(BusinessException.class)
    ResponseEntity<ApiErrorResponse> business(BusinessException exception, HttpServletRequest request) {
        return error(HttpStatus.TOO_MANY_REQUESTS, "BUSINESS-429", exception.getMessage(), request);
    }

    @ExceptionHandler(InvalidOtpException.class)
    ResponseEntity<ApiErrorResponse> invalidOtp(InvalidOtpException exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "OTP-400", exception.getMessage(), request);
    }

    @ExceptionHandler(ExpiredOtpException.class)
    ResponseEntity<ApiErrorResponse> expiredOtp(ExpiredOtpException exception, HttpServletRequest request) {
        return error(HttpStatus.GONE, "OTP-410", exception.getMessage(), request);
    }

    @ExceptionHandler(InvalidInvoiceStatusException.class)
    ResponseEntity<ApiErrorResponse> invalidInvoiceStatus(InvalidInvoiceStatusException exception,
                                                          HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, "FACT-409", exception.getMessage(), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ApiErrorResponse> integrity(HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, "DATA-409", "El registro entra en conflicto con un dato existente.", request);
    }

    @ExceptionHandler(DocumentGenerationException.class)
    ResponseEntity<ApiErrorResponse> document(DocumentGenerationException exception, HttpServletRequest request) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "DOC-500", exception.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiErrorResponse> validation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("La solicitud contiene datos inválidos.");
        return error(HttpStatus.BAD_REQUEST, "REQUEST-400", message, request);
    }

    @ExceptionHandler({HandlerMethodValidationException.class, IllegalArgumentException.class})
    ResponseEntity<ApiErrorResponse> invalidParameters(Exception exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "REQUEST-400", exception.getMessage(), request);
    }

    private ResponseEntity<ApiErrorResponse> error(HttpStatus status, String code, String message,
                                                   HttpServletRequest request) {
        return ResponseEntity.status(status).body(
                new ApiErrorResponse(false, code, message, Instant.now(), request.getRequestURI()));
    }

}

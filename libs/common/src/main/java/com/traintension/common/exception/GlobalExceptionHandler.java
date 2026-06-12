package com.traintension.common.exception;

import com.traintension.common.exception.custom.SecureException;
import com.traintension.common.utils.user.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import jakarta.validation.UnexpectedTypeException;
import jakarta.validation.ConstraintViolationException;

import java.util.function.Supplier;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SecureException.class)
    public ResponseEntity<Void> handleSecure(SecureException e) {
        if (e.getLogMessage() != null) {
            log.error("reqId={} | userId={} | userEmail={} | userRoles ={} | status={} | {}",
                    UserContext.getRequestId(),
                    tryGet(UserContext::getId),
                    tryGet(UserContext::getEmail),
                    tryGet(UserContext::getRoles),
                    e.getHttpStatus().value(),
                    e.getLogMessage());
        }
        return ResponseEntity.status(e.getHttpStatus()).build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(MethodArgumentNotValidException e) {
            return ResponseEntity
            .badRequest()
            .body(ValidationErrorResponse.of(e.getBindingResult()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Void> handleConstraintViolation(ConstraintViolationException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(UnexpectedTypeException.class)
    public ResponseEntity<Void> handleUnexpectedType(UnexpectedTypeException e) {
        log.error("reqId={} | userId={} | userEmail={} | userRoles={} | status={} | {}",
                UserContext.getRequestId(),
                tryGet(UserContext::getId),
                tryGet(UserContext::getEmail),
                tryGet(UserContext::getRoles),
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage());
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Void> handleNotReadable(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Void> handleMissingParam(MissingServletRequestParameterException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Void> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResource(NoResourceFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<Void> handleRestClientResponse(RestClientResponseException e) {
        log.error("reqId={} | userId={} | userEmail={} | userRoles={} | status={} | RestClientError: {}",
                UserContext.getRequestId(),
                tryGet(UserContext::getId),
                tryGet(UserContext::getEmail),
                tryGet(UserContext::getRoles),
                e.getStatusCode().value(),
                e.getMessage());
        return ResponseEntity.status(e.getStatusCode()).build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> handleUnexpected(Exception e) {
        log.error("reqId={} | userId={} | userEmail={} | userRoles={} | status={} | {}",
                UserContext.getRequestId(),
                tryGet(UserContext::getId),
                tryGet(UserContext::getEmail),
                tryGet(UserContext::getRoles),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getMessage(), e);
        return ResponseEntity.internalServerError().build();
    }

    private static Object tryGet(Supplier<?> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return "-";
        }
    }
}

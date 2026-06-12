package com.traintension.core.common.util;

import com.traintension.common.utils.user.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.jooq.exception.DataAccessException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.function.Supplier;

@RestControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DatabaseExceptionHandler {

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Void> handleDatabaseExceptions(DataAccessException e) {
        String sqlState = e.sqlState();

        return switch (sqlState) {
            // Client duplicate veri gönderdi
            case "23505" -> ResponseEntity.status(HttpStatus.CONFLICT).build();

            // Client var olmayan bir kaydı referans gösterdi
            case "23503" -> ResponseEntity.status(HttpStatus.NOT_FOUND).build();

            // Validation katmanını atlatmış — server hatası
            case "23502" -> {
                log.error("reqId={} | userId={} | userEmail={} | userRoles={} | status={} | Not-null violation, validation layer missed | {}",
                        UserContext.getRequestId(),
                        tryGet(UserContext::getId),
                        tryGet(UserContext::getEmail),
                        tryGet(UserContext::getRoles),
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        e.getMessage(), e);
                yield ResponseEntity.internalServerError().build();
            }

            // Deadlock veya serialization hatası — client yeniden deneyebilir
            case "40001", "40P01" -> ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();

            // Beklenmedik DB hatası
            default -> {
                log.error("reqId={} | userId={} | userEmail={} | userRoles={} | status={} | sqlState={} | {}",
                        UserContext.getRequestId(),
                        tryGet(UserContext::getId),
                        tryGet(UserContext::getEmail),
                        tryGet(UserContext::getRoles),
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        sqlState,
                        e.getMessage(), e);
                yield ResponseEntity.internalServerError().build();
            }
        };
    }

    private static Object tryGet(Supplier<?> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return "-";
        }
    }
}
package com.traintension.common.exception.custom;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class SecureException extends RuntimeException {
    private final String logMessage;
    private final HttpStatus httpStatus;

    public SecureException(HttpStatus httpStatus, String logMessage, Object... args) {
        this.logMessage = formatMessage(logMessage, args);
        this.httpStatus = httpStatus;
    }

    public SecureException(HttpStatus httpStatus) {
        this.logMessage = null;
        this.httpStatus = httpStatus;
    }

    private static String formatMessage(String str, Object... args) {
        if (str.isEmpty()) {
            return null;
        }

        for (Object arg : args) {
            str = str.replaceFirst("\\{}", String.valueOf(arg));
        }
        return str;

    }
}

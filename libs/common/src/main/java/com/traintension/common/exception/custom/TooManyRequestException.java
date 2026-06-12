package com.traintension.common.exception.custom;

import org.springframework.http.HttpStatus;

public class TooManyRequestException extends SecureException {
    public TooManyRequestException(String logMessage, Object... args) {
        super(HttpStatus.TOO_MANY_REQUESTS, logMessage, args);
    }

    public TooManyRequestException() {
        super(HttpStatus.TOO_MANY_REQUESTS);
    }
}

package com.traintension.common.exception.custom;

import org.springframework.http.HttpStatus;

public class ConflictException extends SecureException {
    public ConflictException(String logMessage, Object... args) {
        super(HttpStatus.CONFLICT, logMessage, args);
    }
    public ConflictException() {
        super(HttpStatus.CONFLICT);
    }
}

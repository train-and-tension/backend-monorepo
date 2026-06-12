package com.traintension.common.exception.custom;

import org.springframework.http.HttpStatus;

public class NotFoundException extends SecureException {
    public NotFoundException(String logMessage, Object... args) {
        super(HttpStatus.NOT_FOUND, logMessage, args);
    }
    public NotFoundException() {
        super(HttpStatus.NOT_FOUND);
    }
}

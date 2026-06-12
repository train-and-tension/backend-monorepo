package com.traintension.common.exception.custom;

import org.springframework.http.HttpStatus;

public class BadRequestException extends SecureException {
    public BadRequestException(String logMessage, Object... args) {
        super(HttpStatus.BAD_REQUEST, logMessage, args);
    }
    public BadRequestException() {
        super(HttpStatus.BAD_REQUEST);
    }
}
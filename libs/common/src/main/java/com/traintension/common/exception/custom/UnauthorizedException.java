package com.traintension.common.exception.custom;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends SecureException {
    public UnauthorizedException(String logMessage, Object... args) {
        super(HttpStatus.UNAUTHORIZED, logMessage, args);
    }
    public UnauthorizedException() {
        super(HttpStatus.UNAUTHORIZED);
    }
}

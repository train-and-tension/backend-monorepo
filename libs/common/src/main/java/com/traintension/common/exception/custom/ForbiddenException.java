package com.traintension.common.exception.custom;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends SecureException {
    public ForbiddenException(String logMessage, Object... args) {
        super(HttpStatus.FORBIDDEN, logMessage, args);
    }

    public ForbiddenException() {
        super(HttpStatus.FORBIDDEN);
    }
}

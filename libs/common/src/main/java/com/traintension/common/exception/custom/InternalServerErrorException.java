package com.traintension.common.exception.custom;

import org.springframework.http.HttpStatus;

public class InternalServerErrorException extends SecureException {
    public InternalServerErrorException(String logMessage, Object... args) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, logMessage, args);
    }

    public InternalServerErrorException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

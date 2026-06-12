package com.traintension.common.exception.custom;

import org.springframework.http.HttpStatus;

public class ServiceUnavailableException extends SecureException {
    public ServiceUnavailableException(String logMessage, Object... args) {
        super(HttpStatus.SERVICE_UNAVAILABLE, logMessage, args);
    }

    public ServiceUnavailableException() {
        super(HttpStatus.SERVICE_UNAVAILABLE);
    }
}

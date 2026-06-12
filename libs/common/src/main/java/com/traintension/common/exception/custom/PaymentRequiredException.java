package com.traintension.common.exception.custom;

import org.springframework.http.HttpStatus;

public class PaymentRequiredException extends SecureException {
    public PaymentRequiredException(String logMessage, Object... args) {
        super(HttpStatus.PAYMENT_REQUIRED, logMessage, args);
    }

    public PaymentRequiredException() {
        super(HttpStatus.PAYMENT_REQUIRED);
    }
}

package com.traintension.core.common.annotations.UUIDv7Validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UuidV7Validator.class)
public @interface ValidUUIDv7 {
    String message() default "Invalid UUIDv7";
    long maxFutureMinutes() default 5;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
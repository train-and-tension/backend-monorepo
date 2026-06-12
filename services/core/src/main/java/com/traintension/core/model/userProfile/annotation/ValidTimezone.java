package com.traintension.core.model.userProfile.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.ZoneId;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidTimezone.ValidTimezoneValidator.class)
public @interface ValidTimezone {
    String message() default "Invalid timezone";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class ValidTimezoneValidator implements ConstraintValidator<ValidTimezone, String> {
        @Override
        public boolean isValid(String value, ConstraintValidatorContext ctx) {
            if (value == null) return true;
            return ZoneId.getAvailableZoneIds().contains(value);
        }
    }
}


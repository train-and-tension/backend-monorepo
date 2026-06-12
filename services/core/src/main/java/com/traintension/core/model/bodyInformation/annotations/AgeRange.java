package com.traintension.core.model.bodyInformation.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDate;
import java.time.Period;

@Constraint(validatedBy = AgeRange.AgeRangeValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AgeRange {
    String message() default "Age must be between 13 and 120";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class AgeRangeValidator implements ConstraintValidator<AgeRange, LocalDate> {

        @Override
        public boolean isValid(LocalDate birthDate, ConstraintValidatorContext ctx) {
            if (birthDate == null) return true; // @NotNull zaten yakalar
            int age = Period.between(birthDate, LocalDate.now()).getYears();
            return age >= 13 && age <= 120;
        }
    }
}
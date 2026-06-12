package com.traintension.core.model.bodyInformation.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.*;
import java.time.LocalDate;
import java.time.Period;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidAge.AgeValidator.class)
@Documented
public @interface ValidAge {
    String message() default "Age must be between {min} and {max} years";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int min() default 13;
    int max() default 120;

    class AgeValidator implements ConstraintValidator<ValidAge, LocalDate> {
        private int min;
        private int max;

        @Override
        public void initialize(ValidAge constraintAnnotation) {
            this.min = constraintAnnotation.min();
            this.max = constraintAnnotation.max();
        }

        @Override
        public boolean isValid(LocalDate birthDate, ConstraintValidatorContext context) {
            if (birthDate == null) {
                return true;
            }

            LocalDate today = LocalDate.now();

            int age = Period.between(birthDate, today).getYears();
            return age >= min && age <= max;
        }
    }
}
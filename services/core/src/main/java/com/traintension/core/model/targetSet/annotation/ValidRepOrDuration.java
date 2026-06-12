package com.traintension.core.model.targetSet.annotation;

import com.traintension.core.model.targetSet.TargetSetDTO;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidRepOrDuration.ValidRepOrDurationValidator.class)
public @interface ValidRepOrDuration {

    enum Mode { CREATE, UPDATE }

    Mode mode() default Mode.CREATE;

    String message() default "Invalid repCount and duration combination";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class ValidRepOrDurationValidator implements ConstraintValidator<ValidRepOrDuration, TargetSetDTO.RepOrDuration> {

        private Mode mode;

        @Override
        public void initialize(ValidRepOrDuration annotation) {
            this.mode = annotation.mode();
        }

        @Override
        public boolean isValid(TargetSetDTO.RepOrDuration value, ConstraintValidatorContext ctx) {
            boolean hasRep = value.repCount() != null && value.repCount() > 0;
            boolean hasDuration = value.duration() != null && value.duration() > 0;

            return switch (mode) {
                case CREATE -> hasRep ^ hasDuration;
                case UPDATE -> !(hasRep && hasDuration);
            };
        }
    }
}

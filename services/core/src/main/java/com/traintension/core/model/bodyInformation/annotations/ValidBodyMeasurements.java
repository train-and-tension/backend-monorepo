package com.traintension.core.model.bodyInformation.annotations;

import com.traintension.core.generated.enums.UnitSystem;
import com.traintension.core.model.bodyInformation.BodyInformationDTO.*;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;

@Constraint(validatedBy = {
        ValidBodyMeasurements.BodyMeasurementsValidator.class,
        ValidBodyMeasurements.UpdateMeasurementsValidator.class
})
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBodyMeasurements {
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class ValidationHelper {
        static boolean outOfRange(BigDecimal value, String min, String max) {
            return value.compareTo(new BigDecimal(min)) < 0
                    || value.compareTo(new BigDecimal(max)) > 0;
        }

        static void addViolation(ConstraintValidatorContext ctx, String field, String message) {
            ctx.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(field)
                    .addConstraintViolation();
        }

        static boolean validate(BigDecimal height, BigDecimal weight, UnitSystem unit, ConstraintValidatorContext ctx) {
            if (unit == null) {
                if (height != null || weight != null) {
                    ctx.disableDefaultConstraintViolation();
                    addViolation(ctx, "unit", "Unit must be provided when height or weight is specified");
                    return false;
                }
                return true;
            }

            boolean valid = true;
            ctx.disableDefaultConstraintViolation();

            if (unit == UnitSystem.METRIC) {
                if (height != null && outOfRange(height, "50.0", "250.0")) {
                    addViolation(ctx, "height", "Height must be between 50 and 250 cm");
                    valid = false;
                }
                if (weight != null && outOfRange(weight, "20.0", "500.0")) {
                    addViolation(ctx, "weight", "Weight must be between 20 and 500 kg");
                    valid = false;
                }
            } else {
                if (height != null && outOfRange(height, "20.0", "98.0")) {
                    addViolation(ctx, "height", "Height must be between 20 and 98 inches");
                    valid = false;
                }
                if (weight != null && outOfRange(weight, "44.0", "1100.0")) {
                    addViolation(ctx, "weight", "Weight must be between 44 and 1100 lbs");
                    valid = false;
                }
            }
            return valid;
        }
    }

    class BodyMeasurementsValidator
            implements ConstraintValidator<ValidBodyMeasurements, CreatePersonalRequest> {

        @Override
        public boolean isValid(CreatePersonalRequest req, ConstraintValidatorContext ctx) {
            if (req.height() == null && req.weight() == null) return true;
            return ValidationHelper.validate(req.height(), req.weight(), req.unit(), ctx);
        }
    }

    class UpdateMeasurementsValidator
            implements ConstraintValidator<ValidBodyMeasurements, UpdateMeasurementsRequest> {

        @Override
        public boolean isValid(UpdateMeasurementsRequest req, ConstraintValidatorContext ctx) {
            if (req.height() == null && req.weight() == null) return true;
            return ValidationHelper.validate(req.height(), req.weight(), req.unit(), ctx);
        }
    }
}
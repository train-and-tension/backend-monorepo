package com.traintension.core.model.workoutDay.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueOrderNumbersValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueOrderNumbers {
    String message() default "Duplicate orderNumber values are not allowed in the update request.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
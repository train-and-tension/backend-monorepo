package com.traintension.core.common.annotations.enumValidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ValidEnumValidator implements ConstraintValidator<ValidEnum, Object> {
    private List<String> acceptedValues;

    @Override
    public void initialize(ValidEnum annotation) {
        acceptedValues = Stream.of(annotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        String stringValue;
        if (value instanceof Enum<?>) {
            stringValue = ((Enum<?>) value).name();
        } else {
            stringValue = value.toString();
        }

        if (acceptedValues.contains(stringValue)) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                "Invalid value '" + stringValue + "'. Accepted values are: " + acceptedValues
        ).addConstraintViolation();

        return false;
    }
}

package com.traintension.common.exception;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record ValidationErrorResponse(
        Map<String, List<String>> errors
) {
    public static ValidationErrorResponse of(BindingResult bindingResult) {
        Map<String, List<String>> errors = bindingResult.getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ));
        return new ValidationErrorResponse(errors);
    }
}
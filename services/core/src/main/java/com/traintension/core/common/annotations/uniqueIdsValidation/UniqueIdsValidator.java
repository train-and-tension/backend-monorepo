package com.traintension.core.common.annotations.uniqueIdsValidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.UUID;

public class UniqueIdsValidator implements ConstraintValidator<UniqueIds, Collection<?>> {

    @Override
    public boolean isValid(Collection<?> items, ConstraintValidatorContext context) {
        if (items == null || items.isEmpty()) {
            return true;
        }

        long uniqueIdCount = items.stream()
                .map(item -> {
                    if (item instanceof HasId hasId) return hasId.id();
                    if (item instanceof UUID uuid) return uuid;
                    return item;
                })
                .filter(java.util.Objects::nonNull)
                .distinct()
                .count();

        return uniqueIdCount == items.size();
    }
}
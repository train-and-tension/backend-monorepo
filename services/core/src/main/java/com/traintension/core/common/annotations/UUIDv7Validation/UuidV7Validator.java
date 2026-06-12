package com.traintension.core.common.annotations.UUIDv7Validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.regex.Pattern;

public class UuidV7Validator implements ConstraintValidator<ValidUUIDv7, UUID> {

    private static final Pattern PATTERN = Pattern.compile(
            "^[0-9a-f]{8}-[0-9a-f]{4}-7[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$",
            Pattern.CASE_INSENSITIVE
    );

    private long maxFutureMinutes;

    @Override
    public void initialize(ValidUUIDv7 annotation) {
        this.maxFutureMinutes = annotation.maxFutureMinutes();
    }

    @Override
    public boolean isValid(UUID uuid, ConstraintValidatorContext ctx) {
        if (uuid == null) return false;
        if (!PATTERN.matcher(uuid.toString()).matches()) return false;

        long embeddedMs = uuid.getMostSignificantBits() >>> 16;
        Instant uuidInstant = Instant.ofEpochMilli(embeddedMs);
        Instant maxAllowed = Instant.now().plus(maxFutureMinutes, ChronoUnit.MINUTES);

        return !uuidInstant.isAfter(maxAllowed);
    }
}

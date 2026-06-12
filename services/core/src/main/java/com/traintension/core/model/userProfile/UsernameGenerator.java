package com.traintension.core.model.userProfile;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;

import java.text.Normalizer;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@RequiredArgsConstructor
public class UsernameGenerator {

public static String generate(String firstName, String lastName, int attempt) {
    boolean noName = (firstName == null || firstName.isBlank()) && (lastName == null || lastName.isBlank());

    if (noName) {
        return "user@" + shortUuid();
    }

    String combined = ((firstName != null ? firstName : "") + (lastName != null ? lastName : "")).trim();
    String base = normalize(combined);

    if (attempt >= 5) {
        return base + "@" + shortUuid();
    }

    return base + "@" + ThreadLocalRandom.current().nextInt(10000000, 99999999);
}

private static String shortUuid() {
    return UuidCreator.getTimeOrderedEpoch()
            .toString()
            .replace("-", "")
            .substring(0, 8);
}

    private static String normalize(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]", "")
                .trim();
    }
}

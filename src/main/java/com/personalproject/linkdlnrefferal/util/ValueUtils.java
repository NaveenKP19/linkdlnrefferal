package com.personalproject.linkdlnrefferal.util;

import java.util.List;

public final class ValueUtils {

    private ValueUtils() {
    }

    public static String clean(String value) {
        return value == null || value.isBlank() ? "" : value.trim();
    }

    public static String clean(String value, String fallback) {
        String cleaned = clean(value);
        return cleaned.isEmpty() ? fallback : cleaned;
    }

    public static List<String> cleanList(List<String> values) {
        if (values == null) {
            return List.of();
        }

        return values.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(String::trim)
                .toList();
    }
}

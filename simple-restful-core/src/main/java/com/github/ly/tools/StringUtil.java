package com.github.ly.tools;

import java.util.Objects;

public final class StringUtil {
    private StringUtil() {
    }


    public static boolean isBlank(String target) {
        return Objects.isNull(target) || target.trim().isEmpty();
    }
}

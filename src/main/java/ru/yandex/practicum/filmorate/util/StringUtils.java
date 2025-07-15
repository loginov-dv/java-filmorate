package ru.yandex.practicum.filmorate.util;

public final class StringUtils {
    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isBlank();
    }
}

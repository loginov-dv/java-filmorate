package ru.yandex.practicum.filmorate.util;

// Утилитарный класс для работы с объектами класса String
public final class StringUtils {
    // Указывает, является ли строка null или пустой строкой ("").
    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isBlank();
    }
}

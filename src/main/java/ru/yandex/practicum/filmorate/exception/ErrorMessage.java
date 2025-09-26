package ru.yandex.practicum.filmorate.exception;

import lombok.Data;
import lombok.NonNull;

// Сообщение об ошибке
@Data
public class ErrorMessage {
    // Текст сообщения
    @NonNull
    private String error;
}

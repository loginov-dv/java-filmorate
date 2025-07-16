package ru.yandex.practicum.filmorate.exception;

// Исключение, возникающее при неуспешной валидации входных данных
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}

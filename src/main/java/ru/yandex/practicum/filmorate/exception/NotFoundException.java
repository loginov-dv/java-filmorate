package ru.yandex.practicum.filmorate.exception;

// Исключение, возникающее при отсутствии запрашиваемого объекта
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}

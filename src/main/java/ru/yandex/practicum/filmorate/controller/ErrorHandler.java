package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.ErrorMessage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

// RestControllerAdvice для обработки исключений и преобразования их в HTTP-ответы
@RestControllerAdvice
@ResponseBody
public class ErrorHandler {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorMessage> handleValidationException(ValidationException ex) {
        return ResponseEntity.badRequest().body(new ErrorMessage(ex.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorMessage> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage(ex.getMessage()));
    }
}

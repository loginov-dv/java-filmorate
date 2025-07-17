package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

// Класс-валидатор для кастомной аннотации ReleaseDate
public class ReleaseDateValidator implements ConstraintValidator<ReleaseDate, LocalDate> {
    // Самая ранняя возможная дата релиза фильма
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext context) {
        if (releaseDate == null) {
            return false;
        }
        return !releaseDate.isBefore(MIN_RELEASE_DATE);
    }
}

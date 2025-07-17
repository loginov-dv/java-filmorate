package ru.yandex.practicum.filmorate.model;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Кастомная аннотация для проверки даты
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReleaseDateValidator.class)
public @interface ReleaseDate {
    String message() default "Дата релиза — не раньше 28 декабря 1895 года";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

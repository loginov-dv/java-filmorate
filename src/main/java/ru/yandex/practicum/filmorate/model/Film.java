package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

// Модель данных для описания фильма
@Data
@Builder
@EqualsAndHashCode(exclude = {"id", "description"})
public class Film {
    // Идентификатор
    private Integer id;
    // Название
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    // Описание
    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    @NotBlank(message = "Описание должно быть заполнено")
    private String description;
    // Дата релиза
    @ReleaseDate
    private LocalDate releaseDate;
    // Продолжительность
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private int duration;
}

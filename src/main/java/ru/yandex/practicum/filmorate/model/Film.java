package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

// Модель данных для описания фильма
@Data
@Builder
public class Film {
    // Идентификатор
    private Integer id;
    // Название
    @NotNull
    @NotBlank
    private String name;
    // Описание
    @Size(max = 200)
    private String description;
    // Дата релиза
    private LocalDate releaseDate;
    // Продолжительность
    @Positive
    private int duration;
}

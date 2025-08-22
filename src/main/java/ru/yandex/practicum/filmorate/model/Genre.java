package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

// Модель данных для описания жанра фильма
@Data
@Builder
public class Genre {
    // Идентификатор
    private Integer id;
    // Наименование
    @NotBlank
    private String name;
}

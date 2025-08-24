package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

// Модель данных для описания рейтинга Ассоциации кинокомпаний
@Data
public class MpaRating {
    // Идентификатор
    private Integer id;
    // Наименование
    private String name;
}

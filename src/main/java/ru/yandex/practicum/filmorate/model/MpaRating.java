package ru.yandex.practicum.filmorate.model;

import lombok.Data;

// Модель данных для описания рейтинга Ассоциации кинокомпаний
@Data
public class MpaRating {
    // Идентификатор
    private Integer id;
    // Наименование
    private String name;
}

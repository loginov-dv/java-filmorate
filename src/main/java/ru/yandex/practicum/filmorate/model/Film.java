package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

// Модель данных для описания фильма
@Getter
@Setter
@EqualsAndHashCode(exclude = {"id", "description"})
@ToString
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
    // MPA-рейтинг
    private MpaRating rating;
    // Жанры
    private Set<Genre> genres;
}

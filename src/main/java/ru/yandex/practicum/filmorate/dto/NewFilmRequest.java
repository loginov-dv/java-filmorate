package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.ReleaseDate;

import java.time.LocalDate;
import java.util.Set;

// Класс, содержащий данные, необходимые для создания нового фильма
@Data
public class NewFilmRequest {
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    @NotBlank(message = "Описание должно быть заполнено")
    private String description;
    @ReleaseDate
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private int duration;
    @NotNull(message = "Рейтинг должен быть указан")
    private MpaIdDto mpa;
    private Set<GenreIdDto> genres;
    private Set<DirectorIdDto> directors;
}

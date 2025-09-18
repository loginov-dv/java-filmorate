package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.ReleaseDate;

import java.time.LocalDate;
import java.util.Set;

// Класс, содержащий данные для обновления фильма
@Data
public class UpdateFilmRequest {
    @NotNull(message = "Не указан id")
    private Integer id;
    @Pattern(regexp = ".+", message = "Название не может быть пустым")
    private String name;
    @Pattern(regexp = ".+", message = "Описание не может быть пустым")
    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;
    @ReleaseDate
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private Integer duration;
    private Set<DirectorIdDto> directors;

    public boolean hasName() {
        return name != null;
    }

    public boolean hasDescription() {
        return description != null;
    }

    public boolean hasReleaseDate() {
        return releaseDate != null;
    }

    public boolean hasDuration() {
        return duration != null;
    }

    public boolean hasDirectors() {
        return directors != null;
    }
}

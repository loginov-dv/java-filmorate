package ru.yandex.practicum.filmorate.dto;

import lombok.Data;
import ru.yandex.practicum.filmorate.util.StringUtils;

import java.time.LocalDate;

// Класс, содержащий данные для обновления фильма
@Data
public class UpdateFilmRequest {
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;

    public boolean hasName() {
        return !StringUtils.isNullOrEmpty(name);
    }

    public boolean hasDescription() {
        return !StringUtils.isNullOrEmpty(description);
    }

    public boolean hasReleaseDate() {
        return releaseDate != null;
    }

    public boolean hasDuration() {
        return duration != null;
    }
}

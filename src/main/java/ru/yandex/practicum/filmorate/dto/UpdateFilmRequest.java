package ru.yandex.practicum.filmorate.dto;

import lombok.Data;
import ru.yandex.practicum.filmorate.util.StringUtils;

import java.time.LocalDate;

// TODO: check validation
// Класс, содержащий данные для обновления фильма
@Data
public class UpdateFilmRequest {
    private String name;
    // TODO: check size and null/empty
    private String description;
    // TODO: check release date
    private LocalDate releaseDate;
    // TODO: check null
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

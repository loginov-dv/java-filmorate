package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// Класс, содержащий данные для обновления режиссёра
@Data
public class UpdateDirectorRequest {
    @NotNull(message = "Не указан id")
    private Integer id;
    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    public boolean hasName() {
        return name != null;
    }
}

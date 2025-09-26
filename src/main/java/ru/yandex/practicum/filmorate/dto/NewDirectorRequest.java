package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// Класс, содержащий данные, необходимые для создания нового режиссёра
@Data
public class NewDirectorRequest {
    @NotBlank(message = "Имя не может быть пустым")
    private String name;
}

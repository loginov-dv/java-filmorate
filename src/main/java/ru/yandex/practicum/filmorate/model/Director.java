package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

// Модель данных для описания режиссёра
@Data
@EqualsAndHashCode(of = {"id"})
public class Director {
    private Integer id;
    @NotBlank(message = "Имя не может быть пустым")
    private String name;
}

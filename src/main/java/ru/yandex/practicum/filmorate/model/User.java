package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

// Модель данных для описания пользователя
@Data
@Builder
public class User {
    // Идентификатор
    private Integer id;
    // Электронная почта
    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Электронная почта не соответствует формату")
    private String email;
    // Логин
    @NotBlank(message = "Логин не может быть пустым")
    private String login;
    // Имя
    private String name;
    // Дата рождения
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}

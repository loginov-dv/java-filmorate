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
    @NotNull
    @NotBlank
    @Email
    private String email;
    // Логин
    @NotNull
    @NotBlank
    private String login;
    // Имя
    private String name;
    // Дата рождения
    @Past
    private LocalDate birthday;
}

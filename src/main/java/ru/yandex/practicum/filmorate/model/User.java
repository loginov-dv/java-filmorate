package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

// Модель данных для описания пользователя
@Data
@Builder
@EqualsAndHashCode(of = {"email"})
public class User {
    // Идентификатор
    private Integer id;
    // Электронная почта
    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Электронная почта не соответствует формату")
    private String email;
    // Логин
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^\\S+$", message = "Логин не может содержать пробелы")
    private String login;
    // Имя
    private String name;
    // Дата рождения
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}

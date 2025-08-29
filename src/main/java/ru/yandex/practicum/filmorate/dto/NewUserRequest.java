package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

// Класс, содержащий данные, необходимые для создания нового пользователя
@Data
public class NewUserRequest {
    @NotBlank(message = "Электронная почта не может быть пустой")
    @Pattern(regexp = "^\\S+$", message = "Электронная почта не может содержать пробелы")
    @Email(message = "Электронная почта не соответствует формату")
    private String email;
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^\\S+$", message = "Логин не может содержать пробелы")
    private String login;
    private String name;
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}

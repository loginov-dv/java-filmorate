package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

// Класс, содержащий данные для обновления пользователя
@Data
public class UpdateUserRequest {
    @NotNull(message = "Не указан id")
    private Integer id;
    @Pattern(regexp = "^\\S+$", message = "Электронная почта не может быть пустой или содержать пробелы")
    @Email(message = "Электронная почта не соответствует формату")
    private String email;
    @Pattern(regexp = "^\\S+$", message = "Логин не может содержать пробелы")
    private String login;
    private String name;
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    public boolean hasEmail() {
        return email != null;
    }

    public boolean hasLogin() {
        return login != null;
    }

    public boolean hasName() {
        return name != null;
    }

    public boolean hasBirthday() {
        return birthday != null;
    }
}

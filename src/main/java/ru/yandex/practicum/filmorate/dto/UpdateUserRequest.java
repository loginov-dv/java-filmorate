package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import ru.yandex.practicum.filmorate.util.StringUtils;

import java.time.LocalDate;

// Класс, содержащий данные для обновления пользователя
@Data
public class UpdateUserRequest {
    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Электронная почта не соответствует формату")
    private String email;
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^\\S+$", message = "Логин не может содержать пробелы")
    private String login;
    private String name;
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    public boolean hasEmail() {
        return !StringUtils.isNullOrEmpty(email);
    }

    public boolean hasLogin() {
        return !StringUtils.isNullOrEmpty(login);
    }

    public boolean hasName() {
        return !StringUtils.isNullOrEmpty(name);
    }

    public boolean hasBirthday() {
        return birthday != null;
    }
}

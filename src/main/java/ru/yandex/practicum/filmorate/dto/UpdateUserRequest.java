package ru.yandex.practicum.filmorate.dto;

import lombok.Data;
import ru.yandex.practicum.filmorate.util.StringUtils;

import java.time.LocalDate;

// TODO: check validation
// Класс, содержащий данные для обновления пользователя
@Data
public class UpdateUserRequest {
    private Integer id;
    private String email;
    private String login;
    private String name;
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

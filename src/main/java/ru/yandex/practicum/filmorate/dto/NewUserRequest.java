package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

// Класс, содержащий данные, необходимые для создания нового пользователя
@Data
public class NewUserRequest {
    private String email;
    private String login;
    private String name;
    private String birthday;
}

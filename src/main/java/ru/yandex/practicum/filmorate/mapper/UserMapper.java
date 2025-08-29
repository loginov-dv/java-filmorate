package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

// Маппер для класса User
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {
    // Преобразовать User в UserDto
    public static UserDto mapToUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setLogin(user.getLogin());
        dto.setName(user.getName());
        dto.setBirthday(user.getBirthday());

        return dto;
    }

    // Преобразовать NewUserRequest в User
    public static User mapToUser(NewUserRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setLogin(request.getLogin());
        user.setName(request.getName());
        user.setBirthday(request.getBirthday());

        return user;
    }

    // Обновить поля объекта класса User
    public static User updateUserFields(User user, UpdateUserRequest request) {
        if (request.hasEmail()) {
            user.setEmail(request.getEmail());
        }

        if (request.hasLogin()) {
            user.setLogin(request.getLogin());
        }

        if (request.hasName()) {
            user.setName(request.getName());
        }

        if (request.hasBirthday()) {
            user.setBirthday(request.getBirthday());
        }

        return user;
    }
}

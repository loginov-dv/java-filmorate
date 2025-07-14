package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        if (isNullOrEmpty(user.getEmail())) {
            throw new ValidationException("Электронная почта не может быть пустой");
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта должна содержать символ @");
        }
        if (isNullOrEmpty(user.getLogin())) {
            throw new ValidationException("Логин не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        user.setId(getNextId());
        if (isNullOrEmpty(user.getName())) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);

        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());

            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setName(newUser.getName());
            oldUser.setBirthday(newUser.getBirthday());

            return oldUser;
        }

        throw new RuntimeException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private boolean isNullOrEmpty(String string) {
        return string == null || string.isBlank();
    }
}

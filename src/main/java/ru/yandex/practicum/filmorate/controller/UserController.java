package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.StringUtils;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        validate(user);

        String email = user.getEmail();
        if (users.values().stream().anyMatch(item -> item.getEmail().equals(email))) {
            logger.warn("Этот email уже используется");
            throw new ValidationException("Этот email уже используется");
        }

        user.setId(getNextId());
        if (StringUtils.isNullOrEmpty(user.getName())) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        logger.info("Создан пользователь: id = {}, login = {}", user.getId(), user.getLogin());

        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        if (newUser.getId() == null) {
            logger.warn("Не указан id");
            throw new ValidationException("Не указан id");
        }

        if (users.containsKey(newUser.getId())) {
            validate(newUser);

            User oldUser = users.get(newUser.getId());

            String newEmail = newUser.getEmail();
            if (!newEmail.equals(oldUser.getEmail())
                    && users.values().stream().anyMatch(item -> item.getEmail().equals(newEmail))) {
                logger.warn("Этот email уже используется");
                throw new ValidationException("Этот email уже используется");
            }
            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setName(newUser.getName());
            oldUser.setBirthday(newUser.getBirthday());

            logger.info("Изменён пользователь: id = {}, login = {}", oldUser.getId(), oldUser.getLogin());

            return oldUser;
        }

        logger.warn("Пользователь с id = {} не найден", newUser.getId());
        throw new ValidationException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    // Вспомогательный эндпоинт для удаления элементов в мапе (чтобы обеспечить изоляцию тестов)
    @DeleteMapping("/clear")
    public void clear() {
        users.clear();
    }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validate(User user) {
        if (user.getLogin().contains(" ")) {
            logger.warn("Логин не может содержать пробелы");
            throw new ValidationException("Логин не может содержать пробелы");
        }
    }
}

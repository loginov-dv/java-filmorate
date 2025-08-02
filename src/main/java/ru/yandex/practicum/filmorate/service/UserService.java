package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.util.StringUtils;

import java.util.Collection;
import java.util.Optional;

@Service
public class UserService {
    // Хранилище пользователей
    private final UserStorage userStorage;
    // Логгер
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    // Вернуть всех пользователей
    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    // Вернуть пользователя по id
    public Optional<User> getById(int id) {
        return userStorage.getById(id);
    }

    // Создать нового пользователя
    public User create(User user) {
        String email = user.getEmail();
        if (userStorage.getAll().stream().anyMatch(item -> item.getEmail().equals(email))) {
            logger.warn("Этот email уже используется");
            throw new ValidationException("Этот email уже используется");
        }

        user.setId(userStorage.getNextId());
        if (StringUtils.isNullOrEmpty(user.getName())) {
            user.setName(user.getLogin());
        }
        userStorage.create(user);
        logger.info("Создан пользователь: id = {}, login = {}", user.getId(), user.getLogin());

        return user;
    }

    // Изменить пользователя
    public User update(User newUser) {
        if (newUser.getId() == null) {
            logger.warn("Не указан id");
            throw new ValidationException("Не указан id");
        }

        Optional<User> maybeUser = userStorage.getById(newUser.getId());
        if (maybeUser.isPresent()) {
            User oldUser = maybeUser.get();

            String newEmail = newUser.getEmail();
            if (!newEmail.equals(oldUser.getEmail())
                    && userStorage.getAll().stream().anyMatch(item -> item.getEmail().equals(newEmail))) {
                logger.warn("Этот email уже используется");
                throw new ValidationException("Этот email уже используется");
            }
            userStorage.update(newUser);

            logger.info("Изменён пользователь: id = {}, login = {}", newUser.getId(), newUser.getLogin());

            return newUser;
        }

        logger.warn("Пользователь с id = {} не найден", newUser.getId());
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    // Удалить всех пользователей
    public void clear() {
        userStorage.clear();
    }
}
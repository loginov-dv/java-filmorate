package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.util.StringUtils;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

// Сервис по работе с пользователями
@Service
public class UserService {
    // Хранилище пользователей
    private final UserStorage userStorage;
    // Хранилище дружеских связей пользователей
    private final FriendshipStorage friendshipStorage;
    // Логгер
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserStorage userStorage, FriendshipStorage friendshipStorage) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
    }

    // Вернуть всех пользователей
    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    // Вернуть пользователя по id
    public User getById(int id) {
        Optional<User> maybeUser = userStorage.getById(id);

        if (maybeUser.isEmpty()) {
            logger.warn("Пользователь с id = {} не найден", id);
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }

        return maybeUser.get();
    }

    // Создать нового пользователя
    public User create(User user) {
        if (userStorage.getAll().contains(user)) {
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
            if (!newEmail.equals(oldUser.getEmail()) && userStorage.getAll().contains(newUser)) {
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
    public void clearUsers() {
        userStorage.clear();
    }

    // Вспомогательный метод для проверки наличия пользователя с указанным id
    public boolean isPresent(int id) {
        return userStorage.getById(id).isPresent();
    }

    // Добавить дружескую связь между пользователями
    public void addFriend(int userId, int friendId) {
        if (!isPresent(userId)) {
            logger.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (!isPresent(friendId)) {
            logger.warn("Пользователь с id = {} не найден", friendId);
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }
        if (userId == friendId) {
            logger.warn("Нельзя добавить пользователя в друзья к самому себе");
            throw new ValidationException("Нельзя добавить пользователя в друзья к самому себе");
        }
        if (areFriends(userId, friendId)) {
            logger.warn("Пользователи с id = {} и id = {} уже являются друзьями", userId, friendId);
            throw new ValidationException("Пользователи с id = " + userId + " и id = " + friendId +
                    " уже являются друзьями");
        }

        logger.info("Пользователь с id = {} добавил в друзья пользователя с id = {}", userId, friendId);
        friendshipStorage.addFriendship(userId, friendId);
    }

    // Удалить дружескую связь между пользователями
    public void removeFriend(int userId, int friendId) {
        if (!isPresent(userId)) {
            logger.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (!isPresent(friendId)) {
            logger.warn("Пользователь с id = {} не найден", friendId);
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }

        logger.info("Пользователь с id = {} удалил из друзей пользователя с id = {}", userId, friendId);
        friendshipStorage.removeFriendship(userId, friendId);
    }

    // Получить всех друзей пользователя с указанными id
    public Set<User> getFriends(int userId) {
        if (!isPresent(userId)) {
            logger.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        Set<Integer> friendsId = friendshipStorage.getFriends(userId);

        logger.info("Друзья пользователя с id = {}: {}", userId, friendsId);
        return friendsId.stream()
                .map(this::getById)
                .collect(Collectors.toSet());
    }

    // Получить всех общий друзей двух пользователей
    public Set<User> getCommonFriends(int firstUserId, int secondUserId) {
        if (!isPresent(firstUserId)) {
            logger.warn("Пользователь с id = {} не найден", firstUserId);
            throw new NotFoundException("Пользователь с id = " + firstUserId + " не найден");
        }
        if (!isPresent(secondUserId)) {
            logger.warn("Пользователь с id = {} не найден", secondUserId);
            throw new NotFoundException("Пользователь с id = " + secondUserId + " не найден");
        }

        Set<Integer> commonFriendsId = friendshipStorage.getCommonFriends(firstUserId, secondUserId);

        logger.info("Общие друзья пользователей с id = {} и id = {}: {}", firstUserId, secondUserId, commonFriendsId);
        return commonFriendsId.stream()
                .map(this::getById)
                .collect(Collectors.toSet());
    }

    // Проверяет, являются ли друзьями два пользователя с указанными id
    public boolean areFriends(int firstUserId, int secondUserId) {
        return friendshipStorage.areFriends(firstUserId, secondUserId);
    }

    // Удалить все дружеские связи между всеми пользователями
    public void clearFriends() {
        friendshipStorage.clear();
    }
}
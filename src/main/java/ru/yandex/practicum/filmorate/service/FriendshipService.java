package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

import java.util.Set;
import java.util.stream.Collectors;

// Сервис по работе с дружескими связями пользователей
@Service
public class FriendshipService {
    // Хранилище дружеских связей пользователей
    private final FriendshipStorage friendshipStorage;
    // Сервис по работе с пользователями
    private final UserService userService;
    // Логгер
    private static final Logger logger = LoggerFactory.getLogger(FriendshipService.class);

    @Autowired
    public FriendshipService(FriendshipStorage friendshipStorage, UserService userService) {
        this.friendshipStorage = friendshipStorage;
        this.userService = userService;
    }

    // Добавить дружескую связь между пользователями
    public void addFriend(int userId, int friendId) {
        if (!userService.isPresent(userId)) {
            logger.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (!userService.isPresent(friendId)) {
            logger.warn("Пользователь с id = {} не найден", friendId);
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }
        if (userId == friendId) {
            logger.warn("Нельзя добавить пользователя в друзья к самому себе");
            throw new ValidationException("Нельзя добавить пользователя в друзья к самому себе");
        }
        if (areFriends(userId, friendId)) {
            logger.warn("Пользователи уже являются друзьями");
            throw new ValidationException("Пользователи уже являются друзьями");
        }
        friendshipStorage.addFriendship(userId, friendId);
    }

    // Удалить дружескую связь между пользователями
    public void removeFriend(int userId, int friendId) {
        if (!userService.isPresent(userId)) {
            logger.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (!userService.isPresent(friendId)) {
            logger.warn("Пользователь с id = {} не найден", friendId);
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }
        if (!areFriends(userId, friendId)) {
            logger.warn("Пользователи не являются друзьями");
            throw new ValidationException("Пользователи не являются друзьями");
        }
        friendshipStorage.removeFriendship(userId, friendId);
    }

    // Получить всех друзей пользователя с указанными id
    public Set<User> getFriends(int userId) {
        if (!userService.isPresent(userId)) {
            logger.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        Set<Integer> friendsId = friendshipStorage.getFriends(userId);

        return friendsId.stream()
                .map(userService::getById)
                .collect(Collectors.toSet());
    }

    // Получить всех общий друзей двух пользователей
    public Set<User> getCommonFriends(int firstUserId, int secondUserId) {
        if (!userService.isPresent(firstUserId)) {
            logger.warn("Пользователь с id = {} не найден", firstUserId);
            throw new NotFoundException("Пользователь с id = " + firstUserId + " не найден");
        }
        if (!userService.isPresent(secondUserId)) {
            logger.warn("Пользователь с id = {} не найден", secondUserId);
            throw new NotFoundException("Пользователь с id = " + secondUserId + " не найден");
        }

        Set<Integer> commonFriendsId = friendshipStorage.getCommonFriends(firstUserId, secondUserId);

        return commonFriendsId.stream()
                .map(userService::getById)
                .collect(Collectors.toSet());
    }

    // Проверяет, являются ли друзьями два пользователя с указанными id
    public boolean areFriends(int firstUserId, int secondUserId) {
        return friendshipStorage.areFriends(firstUserId, secondUserId);
    }

    public void clear() {
        friendshipStorage.clear();
    }
}
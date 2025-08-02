package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

import java.util.Set;

// Сервис по работе с дружескими связями пользователей
@Service
public class FriendshipService {
    // Хранилище дружеских связей пользователей
    private final FriendshipStorage friendshipStorage;

    @Autowired
    public FriendshipService(FriendshipStorage friendshipStorage) {
        this.friendshipStorage = friendshipStorage;
    }

    // Добавить дружескую связь между пользователями
    public void addFriend(int userId, int friendId) {
        if (userId == friendId) {
            throw new ValidationException("Нельзя добавить пользователя в друзья к самому себе");
        }
        if (areFriends(userId, friendId)) {
            throw new ValidationException("Пользователи уже являются друзьями");
        }
        friendshipStorage.addFriendship(userId, friendId);
    }

    // Удалить дружескую связь между пользователями
    public void removeFriend(int userId, int friendId) {
        if (!areFriends(userId, friendId)) {
            throw new ValidationException("Пользователи не являются друзьями");
        }
        friendshipStorage.removeFriendship(userId, friendId);
    }

    // Получить всех друзей пользователя с указанными id
    public Set<Integer> getFriends(int userId) {
        return friendshipStorage.getFriends(userId);
    }

    // Получить всех общий друзей двух пользователей
    public Set<Integer> getCommonFriends(int firstUserId, int secondUserId) {
        return friendshipStorage.getCommonFriends(firstUserId, secondUserId);
    }

    // Проверяет, являются ли друзьями два пользователя с указанными id
    public boolean areFriends(int firstUserId, int secondUserId) {
        return friendshipStorage.areFriends(firstUserId, secondUserId);
    }
}
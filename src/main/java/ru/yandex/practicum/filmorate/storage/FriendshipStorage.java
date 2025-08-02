package ru.yandex.practicum.filmorate.storage;

import java.util.Set;

// Интерфейс для хранения информации о дружбе между пользователями
public interface FriendshipStorage {
    // Добавить дружескую связь между пользователями
    void addFriendship(int userId, int friendId);
    // Удалить дружескую связь между пользователями
    void removeFriendship(int userId, int friendId);
    // Получить всех друзей пользователя с указанными id
    Set<Integer> getFriends(int userId);
    // Получить всех общий друзей двух пользователей
    Set<Integer> getCommonFriends(int firstUserId, int secondUserId);
}
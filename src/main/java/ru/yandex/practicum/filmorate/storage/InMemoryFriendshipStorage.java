package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

// Класс, реализующий логику хранения дружеских связей между пользователями в оперативной памяти
@Component
public class InMemoryFriendshipStorage implements FriendshipStorage {
    // Множество объектов класса Friendship
    private final Set<Friendship> friendshipSet = new HashSet<>();

    // Добавить дружескую связь между пользователями
    @Override
    public void addFriendship(int userId, int friendId) {
        friendshipSet.add(new Friendship(userId, friendId));
        // Двусторонняя связь
        friendshipSet.add(new Friendship(friendId, userId));
    }

    // Удалить дружескую связь между пользователями
    @Override
    public void removeFriendship(int userId, int friendId) {
        friendshipSet.removeIf(item ->
                (item.getUserId().equals(userId) && item.getFriendId().equals(friendId))
                        || (item.getUserId().equals(friendId) && item.getFriendId().equals(userId)));
    }

    // Получить всех друзей пользователя с указанными id
    @Override
    public Set<Integer> getFriends(int userId) {
        return friendshipSet.stream()
                .filter(item -> item.getUserId().equals(userId))
                .map(Friendship::getFriendId)
                .collect(Collectors.toSet());
    }

    // Получить всех общий друзей двух пользователей
    @Override
    public Set<Integer> getCommonFriends(int firstUserId, int secondUserId) {
        Set<Integer> firstUserFriends = getFriends(firstUserId);
        Set<Integer> secondUserFriends = getFriends(secondUserId);

        return firstUserFriends.stream()
                .filter(secondUserFriends::contains)
                .collect(Collectors.toSet());
    }

    // Проверяет, являются ли друзьями два пользователя с указанными id
    @Override
    public boolean areFriends(int firstUserId, int secondUserId) {
        return friendshipSet.stream()
                .anyMatch(item -> item.getUserId().equals(firstUserId)
                        && item.getFriendId().equals(secondUserId));
    }
}
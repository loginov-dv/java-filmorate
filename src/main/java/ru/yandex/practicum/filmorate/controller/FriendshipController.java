package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FriendshipService;

import java.util.Collection;

// Контроллер по работе с дружескими связями пользователей
@RestController
public class FriendshipController {
    // Сервис по работе с дружескими связями пользователей
    private final FriendshipService friendshipService;

    @Autowired
    public FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    // Эндпоинт PUT /users/{id}/friends/{friendId}
    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id,
                          @PathVariable int friendId) {
        friendshipService.addFriend(id, friendId);
    }

    // Эндпоинт DELETE /users/{id}/friends/{friendId}
    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable int id,
                             @PathVariable int friendId) {
        friendshipService.removeFriend(id, friendId);
    }

    // Эндпоинт GET /users/{id}/friends
    @GetMapping("/users/{id}/friends")
    public Collection<User> getFriends(@PathVariable int id) {
        return friendshipService.getFriends(id);
    }

    // Эндпоинт GET /users/{id}/friends/common/{otherId}
    @GetMapping("/users/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable int id,
                                             @PathVariable int otherId) {
        return friendshipService.getCommonFriends(id, otherId);
    }

    // Вспомогательный эндпоинт DELETE /users/friends/clear для удаления всех дружеских связей
    // (для обеспечения изоляции тестов)
    @DeleteMapping("/users/friends/clear")
    public void clear() {
        friendshipService.clear();
    }
}
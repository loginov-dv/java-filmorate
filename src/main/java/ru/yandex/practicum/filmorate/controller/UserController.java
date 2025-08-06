package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

// Контроллер для работы с пользователями
@RestController
@RequestMapping("/users")
public class UserController {
    // Сервис по работе с пользователями
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Эндпоинт GET /users
    @GetMapping
    public Collection<User> getAll() {
        return userService.getAll();
    }

    // Эндпоинт GET /users/{id}
    @GetMapping("/{id}")
    public User getById(@PathVariable int id) {
        return userService.getById(id);
    }

    // Эндпоинт POST /users
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    // Эндпоинт PUT /users
    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        return userService.update(newUser);
    }

    // Вспомогательный эндпоинт DELETE /users для удаления элементов в хранилище (чтобы обеспечить изоляцию тестов)
    @DeleteMapping("/clear")
    public void clearAllUsers() {
        userService.clearAllUsers();
    }

    // Эндпоинт PUT /users/{id}/friends/{friendId}
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id,
                          @PathVariable int friendId) {
        userService.addFriend(id, friendId);
    }

    // Эндпоинт DELETE /users/{id}/friends/{friendId}
    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable int id,
                             @PathVariable int friendId) {
        userService.removeFriend(id, friendId);
    }

    // Эндпоинт GET /users/{id}/friends
    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable int id) {
        return userService.getFriends(id);
    }

    // Эндпоинт GET /users/{id}/friends/common/{otherId}
    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable int id,
                                             @PathVariable int otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    // Вспомогательный эндпоинт DELETE /users/friends/clear для удаления всех дружеских связей
    // (для обеспечения изоляции тестов)
    @DeleteMapping("/friends/clear")
    public void clearAllFriendships() {
        userService.clearAllFriendships();
    }
}

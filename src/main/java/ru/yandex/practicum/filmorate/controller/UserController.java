package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

// Контроллер для обслуживания пользователей
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

    @GetMapping("/{id}")
    public User getById(@PathVariable int id) {
        return userService.getById(id);
    }

    // Эндпоинт POST /users
    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    // Эндпоинт PUT /users
    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        return userService.update(newUser);
    }

    // Вспомогательный эндпоинт DELETE /users для удаления элементов в мапе (чтобы обеспечить изоляцию тестов)
    @DeleteMapping("/clear")
    public void clear() {
        userService.clear();
    }
}

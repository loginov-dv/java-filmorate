package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    // Логгер
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Эндпоинт GET /users
    @GetMapping
    public List<UserDto> getAll() {
        logger.debug("Вызов эндпоинта GET /users");
        return userService.getAll();
    }

    // Эндпоинт GET /users/{id}
    @GetMapping("/{id}")
    public UserDto getById(@PathVariable @Positive int id) {
        logger.debug("Вызов эндпоинта GET /users/{id}");
        return userService.getById(id);
    }

    // Эндпоинт POST /users
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody NewUserRequest request) {
        logger.debug("Вызов эндпоинта POST /users");
        return userService.create(request);
    }

    // Эндпоинт PUT /users
    @PutMapping
    public UserDto update(@Valid @RequestBody UpdateUserRequest request) {
        logger.debug("Вызов эндпоинта PUT /users");
        return userService.update(request);
    }

    // Эндпоинт PUT /users/{id}/friends/{friendId}
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable @Positive int id,
                          @PathVariable @Positive int friendId) {
        logger.debug("Вызов эндпоинта PUT /users/{id}/friends/{friendId}");
        userService.addFriend(id, friendId);
    }

    // Эндпоинт DELETE /users/{id}/friends/{friendId}
    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFriend(@PathVariable @Positive int id,
                             @PathVariable @Positive int friendId) {
        logger.debug("Вызов эндпоинта DELETE /users/{id}/friends/{friendId}");
        userService.removeFriend(id, friendId);
    }

    // Эндпоинт GET /users/{id}/friends
    @GetMapping("/{id}/friends")
    public List<UserDto> getFriends(@PathVariable @Positive int id) {
        logger.debug("Вызов эндпоинта GET /users/{id}/friends");
        return userService.getFriends(id);
    }

    // Эндпоинт GET /users/{id}/friends/common/{otherId}
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserDto> getCommonFriends(@PathVariable @Positive int id,
                                          @PathVariable @Positive int otherId) {
        logger.debug("Вызов эндпоинта GET /users/{id}/friends/common/{otherId}");
        return userService.getCommonFriends(id, otherId);
    }

    // Эндпоинт GET /users/{id}/feed
    @GetMapping("/{id}/feed")
    public List<EventDto> getFeed(@PathVariable @Positive int id) {
        logger.debug("Вызов эндпоинта GET /users/{id}/feed");
        return userService.getFeed(id);
    }

    @GetMapping("/{id}/recommendations")
    public List<FilmDto> getRecommendations(@PathVariable @Positive int id) {
        logger.debug("Вызов эндпоинта GET /users/{id}/recommendations");
        return userService.getRecommendations(id);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable @Positive int userId) {
        logger.debug("Вызов эндпоинта DELETE /users/{userId}");
        userService.removeUserById(userId);
    }
}

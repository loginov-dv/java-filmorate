package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Сервис по работе с пользователями
@Service
public class UserService {
    // Репозиторий пользователей
    private final UserRepository userRepository;
    // Логгер
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Вернуть всех пользователей
    public List<UserDto> getAll() {
        logger.debug("Запрос на получение всех пользователей");

        return userRepository.getAll().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    // Вернуть пользователя по id
    public UserDto getById(int id) {
        logger.debug("Запрос на получение пользователя с id = {}", id);

        Optional<User> maybeUser = userRepository.getById(id);

        if (maybeUser.isEmpty()) {
            logger.warn("Пользователь с id = {} не найден", id);
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }

        return UserMapper.mapToUserDto(maybeUser.get());
    }

    // Создать нового пользователя
    public UserDto create(NewUserRequest request) {
        logger.debug("Запрос на создания нового пользователя");
        logger.debug("Входные данные: {}", request);

        if (userRepository.getByEmail(request.getEmail()).isPresent()) {
            logger.warn("Этот email уже используется");
            throw new ValidationException("Этот email уже используется");
        }

        User user = UserMapper.mapToUser(request);
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        user = userRepository.create(user);

        logger.info("Создан пользователь: {}", user);
        return UserMapper.mapToUserDto(user);
    }

    // Изменить пользователя
    public UserDto update(UpdateUserRequest request) {
        logger.debug("Запрос на изменение пользователя с id = {}", request.getId());
        logger.debug("Входные данные: {}", request);

        if (request.getId() == null) {
            logger.warn("Не указан id");
            throw new NotFoundException("Не указан id");
        }

        Optional<User> maybeUser = userRepository.getById(request.getId());

        if (maybeUser.isEmpty()) {
            logger.warn("Пользователь с id = {} не найден", request.getId());
            throw new NotFoundException("Пользователь с id = " + request.getId() + " не найден");
        }

        User user = maybeUser.get();
        logger.debug("Исходное состояние: {}", user);

        if (request.getEmail() != null && !user.getEmail().equals(request.getEmail())
                && userRepository.getByEmail(request.getEmail()).isPresent()) {
            logger.warn("Этот email уже используется");
            throw new ValidationException("Этот email уже используется");
        }

        User updatedUser = UserMapper.updateUserFields(user, request);
        updatedUser = userRepository.update(updatedUser);

        logger.info("Изменен пользователь: {}", updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }

    // Добавить дружескую связь между пользователями
    public void addFriend(int userId, int friendId) {
        logger.debug("Запрос на добавление пользователя с id = {} в друзья пользователя с id = {}",
                friendId, userId);

        if (userRepository.getById(userId).isEmpty()) {
            logger.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (userRepository.getById(friendId).isEmpty()) {
            logger.warn("Пользователь с id = {} не найден", friendId);
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }
        if (userId == friendId) {
            logger.warn("Нельзя добавить пользователя в друзья к самому себе");
            throw new ValidationException("Нельзя добавить пользователя в друзья к самому себе");
        }

        if (getFriends(userId).stream().anyMatch(friend -> friend.getId() == friendId)) {
            logger.warn("Пользователи с id = {} и id = {} уже являются друзьями", userId, friendId);
            throw new ValidationException("Пользователи с id = " + userId + " и id = " + friendId +
                    " уже являются друзьями");
        }

        userRepository.addFriend(userId, friendId);
        logger.info("Пользователь с id = {} добавил в друзья пользователя с id = {}", userId, friendId);
    }

    // Удалить дружескую связь между пользователями
    public void removeFriend(int userId, int friendId) {
        logger.debug("Запрос на удаления пользователя с id = {} из друзей пользователя с id = {}",
                friendId, userId);

        if (userRepository.getById(userId).isEmpty()) {
            logger.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (userRepository.getById(friendId).isEmpty()) {
            logger.warn("Пользователь с id = {} не найден", friendId);
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }

        userRepository.removeFriend(userId, friendId);
        logger.info("Пользователь с id = {} удалил из друзей пользователя с id = {}", userId, friendId);
    }

    // Получить всех друзей пользователя с указанными id
    public List<UserDto> getFriends(int userId) {
        logger.debug("Запрос на получение всех друзей пользователя с id = {}", userId);

        if (userRepository.getById(userId).isEmpty()) {
            logger.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        List<User> friends = userRepository.getFriends(userId);

        logger.info("Друзья пользователя с id = {}: {}", userId,
                friends.stream()
                        .map(User::getId)
                        .collect(Collectors.toList()));
        return friends.stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    // Получить всех общий друзей двух пользователей
    public List<UserDto> getCommonFriends(int firstUserId, int secondUserId) {
        logger.debug("Запрос на получение общих друзей пользователей с id = {} и id = {}",
                firstUserId, secondUserId);

        if (userRepository.getById(firstUserId).isEmpty()) {
            logger.warn("Пользователь с id = {} не найден", firstUserId);
            throw new NotFoundException("Пользователь с id = " + firstUserId + " не найден");
        }
        if (userRepository.getById(secondUserId).isEmpty()) {
            logger.warn("Пользователь с id = {} не найден", secondUserId);
            throw new NotFoundException("Пользователь с id = " + secondUserId + " не найден");
        }

        List<User> firstUserFriends = userRepository.getFriends(firstUserId);
        List<User> secondUserFriends = userRepository.getFriends(secondUserId);

        List<User> commonFriends = firstUserFriends.stream()
                        .filter(secondUserFriends::contains)
                        .collect(Collectors.toList());

        logger.info("Общие друзья пользователей с id = {} и id = {}: {}", firstUserId, secondUserId,
                commonFriends.stream()
                        .map(User::getId)
                        .collect(Collectors.toList()));
        return commonFriends.stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public void removeUserById(int userId) {
        userRepository.removeUserById(userId);
    }
}
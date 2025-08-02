package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

// Интерфейс для хранения пользователей
public interface UserStorage {

    // Вернуть всех пользователей
    Collection<User> getAll();

    // Вернуть пользователя по id
    Optional<User> getById(int id);

    // Создать нового пользователя
    void create(User user);

    // Изменить пользователя
    void update(User user);

    // Удалить всех пользователей
    void clear();

    // Получить новый id пользователя
    int getNextId();
}
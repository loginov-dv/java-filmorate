package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// Класс, реализующий логику хранения пользователей в оперативной памяти
@Component
public class InMemoryUserStorage implements UserStorage {
    // Мапа для хранения пользователей
    private final Map<Integer, User> users = new HashMap<>();

    // Вернуть всех пользователей
    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    // Вернуть пользователя по id
    @Override
    public Optional<User> getById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    // Создать нового пользователя
    @Override
    public void create(User user) {
        users.put(user.getId(), user);
    }

    // Изменить пользователя
    @Override
    public void update(User user) {
        users.put(user.getId(), user);
    }

    // Удалить всех пользователей
    @Override
    public void clear() {
        users.clear();
    }

    // Вспомогательный метод для генерации id
    @Override
    public int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
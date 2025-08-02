package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    // Мапа для хранения фильмов
    private final Map<Integer, Film> films = new HashMap<>();

    // Вернуть все фильмы
    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    // Вернуть фильм по id
    @Override
    public Optional<Film> getById(int id) {
        Film film = films.get(id);
        return Optional.ofNullable(film);
    }

    // Создать новый фильм
    @Override
    public void create(Film film) {
        films.put(film.getId(), film);
    }

    // Изменить фильм
    @Override
    public void update(Film film) {
        films.put(film.getId(), film);
    }

    // Удалить все фильмы
    @Override
    public void clear() {
        films.clear();
    }

    // Вспомогательный метод для генерации id
    @Override
    public int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
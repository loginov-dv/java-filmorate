package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// Класс, реализующий хранение фильмов в оперативной памяти
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
        return Optional.ofNullable(films.get(id));
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

    // Поставить лайк фильму
    @Override
    public void putLike(int filmId, int userId) {
        Film film = films.get(filmId);
        film.addLike(userId);
    }

    // Убрать лайк
    @Override
    public void removeLike(int filmId, int userId) {
        Film film = films.get(filmId);
        film.removeLike(userId);
    }

    // Убрать лайки у всех фильмов
    @Override
    public void clearLikes() {
        for (Film film : films.values()) {
            film.removeAllLikes();
        }
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
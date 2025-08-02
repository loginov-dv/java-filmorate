package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

// Интерфейс для хранения фильмов
public interface FilmStorage {

    // Вернуть все фильмы
    Collection<Film> getAll();

    // Вернуть фильм по id
    Optional<Film> getById(int id);

    // Создать новый фильм
    void create(Film film);

    // Изменить фильм
    void update(Film film);

    // Удалить все фильмы
    void clear();

    // Поставить лайк
    void putLike(int filmId, int userId);

    // Убрать лайк
    void removeLike(int filmId, int userId);

    // Убрать лайки у всех фильмов
    void clearLikes();

    // Получить новый id фильма
    int getNextId();
}
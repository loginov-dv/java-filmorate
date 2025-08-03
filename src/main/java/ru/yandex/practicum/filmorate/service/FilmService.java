package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FilmService {
    // Хранилище фильмов
    private final FilmStorage filmStorage;
    // Логгер
    private static final Logger logger = LoggerFactory.getLogger(FilmService.class);
    // Сервис по работе с пользователями
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    // Вернуть все фильмы
    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    // Вернуть фильм по id
    public Film getById(int id) {
        Optional<Film> maybeFilm = filmStorage.getById(id);

        if (maybeFilm.isEmpty()) {
            logger.warn("Фильм с id = {} не найден", id);
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }

        return maybeFilm.get();
    }

    // Создать новый фильм
    public Film create(Film film) {
        film.setId(filmStorage.getNextId());
        filmStorage.create(film);
        logger.info("Создан фильм: id = {}, name = {}", film.getId(), film.getName());

        return film;
    }

    // Изменить фильм
    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            logger.warn("Не указан id");
            throw new ValidationException("Не указан id");
        }

        Optional<Film> maybeFilm = filmStorage.getById(newFilm.getId());
        if (maybeFilm.isPresent()) {
            filmStorage.update(newFilm);
            logger.info("Изменён фильм: id = {}, name = {}", newFilm.getId(), newFilm.getName());

            return newFilm;
        }

        logger.warn("Фильм с id = {} не найден", newFilm.getId());
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    // Поставить лайк
    public void putLike(int filmId, int userId) {
        if (!userService.isPresent(userId)) {
            logger.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (!isPresent(filmId)) {
            logger.warn("Фильм с id = {} не найден", filmId);
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }

        filmStorage.putLike(filmId, userId);
        logger.info("Пользователь с id = {} поставил лайк фильму с id = {}", userId, filmId);
    }

    // Удалить лайк
    public void removeLike(int filmId, int userId) {
        if (!userService.isPresent(userId)) {
            logger.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (!isPresent(filmId)) {
            logger.warn("Фильм с id = {} не найден", filmId);
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }

        filmStorage.removeLike(filmId, userId);
        logger.info("Пользователь с id = {} убрал лайк у фильма с id = {}", userId, filmId);
    }

    // Полуить список из первых count фильмов по количеству лайков
    public Collection<Film> getPopular(int count) {
        if (count <= 0) {
            logger.warn("Количество фильмов должно быть положительным числом. Было передано: {}", count);
            throw new ValidationException("Количество фильмов должно быть положительным числом. " +
                    "Было передано: " + count);
        }

        return filmStorage.getAll().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    // Убрать лайки у всех фильмов
    public void clearLikes() {
        filmStorage.clearLikes();
    }

    // Вспомогательный метод для проверки наличия фильма с указанным id
    public boolean isPresent(int id) {
        return filmStorage.getById(id).isPresent();
    }

    // Удалить все фильмы
    public void clear() {
        filmStorage.clear();
    }
}
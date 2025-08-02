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
import java.util.Optional;

@Service
public class FilmService {
    // Хранилище фильмов
    private final FilmStorage filmStorage;
    // Логгер
    private static final Logger logger = LoggerFactory.getLogger(FilmService.class);

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    // Вернуть все фильмы
    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    // Вернуть фильм по id
    public Optional<Film> getById(int id) {
        return filmStorage.getById(id);
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
            Film oldFilm = maybeFilm.get();

            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());

            logger.info("Изменён фильм: id = {}, name = {}", oldFilm.getId(), oldFilm.getName());

            return oldFilm;
        }

        logger.warn("Фильм с id = {} не найден", newFilm.getId());
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    // Удалить все фильмы
    public void clear() {
        filmStorage.clear();
    }

}
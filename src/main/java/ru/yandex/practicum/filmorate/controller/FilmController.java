package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// Контроллер для обслуживания фильмов
@RestController
@RequestMapping("/films")
public class FilmController {
    // Самая ранняя возможная дата релиза фильма
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    // Логгер
    private static final Logger logger = LoggerFactory.getLogger(FilmController.class);
    // Мапа для хранения фильмов
    private final Map<Integer, Film> films = new HashMap<>();

    // Эндпоинт GET /films
    @GetMapping
    public Collection<Film> getAll() {
        return films.values();
    }

    // Эндпоинт POST /films
    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        validate(film);

        film.setId(getNextId());
        films.put(film.getId(), film);
        logger.info("Создан фильм: id = {}, name = {}", film.getId(), film.getName());

        return film;
    }

    // Эндпоинт PUT /films
    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            logger.warn("Не указан id");
            throw new ValidationException("Не указан id");
        }

        if (films.containsKey(newFilm.getId())) {
            validate(newFilm);

            Film oldFilm = films.get(newFilm.getId());

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

    // Вспомогательный эндпоинт DELETE /films для удаления элементов в мапе (чтобы обеспечить изоляцию тестов)
    @DeleteMapping("/clear")
    public void clear() {
        films.clear();
    }

    // Вспомогательный метод для генерации идентификаторов
    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    // Вспомогательный метод для валидации
    private void validate(Film film) {
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            logger.warn("Дата релиза — не раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
    }
}

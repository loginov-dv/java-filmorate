package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

// Контроллер для обслуживания фильмов
@RestController
@RequestMapping("/films")
public class FilmController {
    // Сервис работы с фильмами
    private final FilmService filmService;
    // Логгер
    private static final Logger logger = LoggerFactory.getLogger(FilmController.class);

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    // Эндпоинт GET /films
    @GetMapping
    public Collection<Film> getAll() {
        return filmService.getAll();
    }

    // Эндпоинт POST /films
    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    // Эндпоинт PUT /films
    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        return filmService.update(newFilm);
    }

    // Вспомогательный эндпоинт DELETE /films для удаления элементов в мапе (чтобы обеспечить изоляцию тестов)
    @DeleteMapping("/clear")
    public void clear() {
        filmService.clear();
    }
}

package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

// Контроллер для работы с фильмами
@RestController
@RequestMapping("/films")
public class FilmController {
    // Сервис работы с фильмами
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    // Эндпоинт GET /films
    @GetMapping
    public Collection<Film> getAll() {
        return filmService.getAll();
    }

    // Эндпоинт GET /films/{id}
    @GetMapping("/{id}")
    public Film getById(@PathVariable int id) {
        return filmService.getById(id);
    }

    // Эндпоинт POST /films
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    // Эндпоинт PUT /films
    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        return filmService.update(newFilm);
    }

    // Эндпоинт PUT /films/{id}/like/{userId}
    @PutMapping("/{id}/like/{userId}")
    public void putLike(@PathVariable int id,
                        @PathVariable int userId) {
        filmService.putLike(id, userId);
    }

    // Эндпоинт DELETE /films/{id}/like/{userId}
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int id,
                           @PathVariable int userId) {
        filmService.removeLike(id, userId);
    }

    // Эндпоинт GET /films/popular/count?=count
    @GetMapping("/popular")
    public Collection<Film> getPopular(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopular(count);
    }

    // Вспомогательный эндпоинт DELETE /films/likes/clear (для удаления всех лайков и изоляции тестов)
    @DeleteMapping("/likes/clear")
    public void clearLikes() {
        filmService.clearLikes();
    }

    // Вспомогательный эндпоинт DELETE /films для удаления элементов в мапе (чтобы обеспечить изоляцию тестов)
    @DeleteMapping("/clear")
    public void clear() {
        filmService.clear();
    }
}

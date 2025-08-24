package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

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
    public List<FilmDto> getAll() {
        return filmService.getAll();
    }

    // Эндпоинт GET /films/{id}
    @GetMapping("/{id}")
    public FilmDto getById(@PathVariable int id) {
        return filmService.getById(id);
    }

    // Эндпоинт POST /films
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto create(@Valid @RequestBody NewFilmRequest request) {
        return filmService.create(request);
    }

    // Эндпоинт PUT /films
    // TODO: valid?
    @PutMapping("/{id}")
    public FilmDto update(@PathVariable int id, @Valid @RequestBody UpdateFilmRequest request) {
        return filmService.update(id, request);
    }

    /*
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

    // Вспомогательный эндпоинт DELETE /films для удаления элементов в хранилище (чтобы обеспечить изоляцию тестов)
    @DeleteMapping("/clear")
    public void clear() {
        filmService.clear();
    }
    */
}

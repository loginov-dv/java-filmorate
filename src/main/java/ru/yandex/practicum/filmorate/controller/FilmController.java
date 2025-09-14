package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

// Контроллер для работы с фильмами
@RestController
@RequestMapping(value = "/films", produces = "application/json;charset=UTF-8")
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
    public List<FilmDto> getAll() {
        logger.debug("Вызов эндпоинта GET /films");
        return filmService.getAll();
    }

    // Эндпоинт GET /films/{id}
    @GetMapping("/{id}")
    public FilmDto getById(@PathVariable int id) {
        logger.debug("Вызов эндпоинта GET /films/{id}");
        return filmService.getById(id);
    }

    // Эндпоинт POST /films
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto create(@Valid @RequestBody NewFilmRequest request) {
        logger.debug("Вызов эндпоинта POST /films");
        return filmService.create(request);
    }

    // Эндпоинт PUT /films
    @PutMapping
    public FilmDto update(@Valid @RequestBody UpdateFilmRequest request) {
        logger.debug("Вызов эндпоинта PUT /films");
        return filmService.update(request);
    }

    // Эндпоинт PUT /films/{id}/like/{userId}
    @PutMapping("/{id}/like/{userId}")
    public void putLike(@PathVariable int id,
                        @PathVariable int userId) {
        logger.debug("Вызов эндпоинта PUT /films/{id}/like/{userId}");
        filmService.putLike(id, userId);
    }

    // Эндпоинт DELETE /films/{id}/like/{userId}
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int id,
                           @PathVariable int userId) {
        logger.debug("Вызов эндпоинта DELETE /films/{id}/like/{userId}");
        filmService.removeLike(id, userId);
    }

    // Эндпоинт GET /films/popular/count?=count
    @GetMapping("/popular")
    public List<FilmDto> getPopular(@RequestParam(defaultValue = "10") int count) {
        logger.debug("Вызов эндпоинта GET /films/popular/count?=count");
        return filmService.getPopular(count);
    }

    // Эндпоинт GET /films/search?query=...&by=...
    @GetMapping("/search")
    public List<FilmDto> search(@RequestParam String query,
                                @RequestParam(name = "by", defaultValue = "title") String by) {
        logger.debug("Вызов эндпоинта GET /films/search");
        return filmService.search(query, by);
    }
}
package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;
    private static final Logger logger = LoggerFactory.getLogger(FilmController.class);
    // Параметры сортировки
    private final List<String> sortParameters = List.of("year", "likes");

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
    public FilmDto getById(@PathVariable @Positive int id) {
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
    public void putLike(@PathVariable @Positive int id,
                        @PathVariable @Positive int userId) {
        logger.debug("Вызов эндпоинта PUT /films/{id}/like/{userId}");
        filmService.putLike(id, userId);
    }

    // Эндпоинт DELETE /films/{id}/like/{userId}
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable @Positive int id,
                           @PathVariable @Positive int userId) {
        logger.debug("Вызов эндпоинта DELETE /films/{id}/like/{userId}");
        filmService.removeLike(id, userId);
    }

    // Эндпоинт GET /films/popular/
    @GetMapping("/popular")
    public List<FilmDto> getPopular(@RequestParam(defaultValue = "10") @Positive int count,
                                    @RequestParam(required = false) @Positive Integer genreId,
                                    @RequestParam(required = false) Integer year) {
        logger.debug("Вызов эндпоинта GET /films/popular/");
        return filmService.getPopular(count, genreId, year);
    }

    // Эндпоинт DELETE /films/{filmId}
    @DeleteMapping("/{filmId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFilm(@PathVariable @Positive int filmId) {
        logger.debug("Вызов эндпоинта DELETE /films/{filmId}");
        filmService.removeFilmById(filmId);
    }

    // Эндпоинт GET /films/director/{directorId}?sortBy=[year,likes]
    @GetMapping("/director/{directorId}")
    public List<FilmDto> getDirectorsFilm(@PathVariable @Positive int directorId,
                                          @RequestParam String sortBy) {
        logger.debug("Вызов эндпоинта GET /films/director/{directorId}?sortBy=[year,likes]");
        if (!sortParameters.contains(sortBy)) {
            logger.warn("Переданный параметр сортировки sortBy = {} не поддерживается", sortBy);
            throw new ValidationException("Переданный параметр сортировки sortBy = " + sortBy + " не поддерживается");
        }
        return filmService.search(directorId, sortBy);
    }

    // Эндпоинт GET /films/search?query=...&by=director,title — поиск по подстроке, сортировка по популярности
    @GetMapping("/search")
    public List<FilmDto> search(@RequestParam String query,
                                @RequestParam String by) {
        logger.debug("Вызов эндпоинта GET /films/search");
        return filmService.search(query, by);
    }

    // Эндпоинт GET /films/common?userId={userId}&friendId={friendId}
    @GetMapping("/common")
    public List<FilmDto> getCommon(@RequestParam @Positive int userId,
                                   @RequestParam @Positive int friendId) {
        logger.debug("Вызов эндпоинта GET /films/common?userId={}&friendId={}", userId, friendId);
        return filmService.getCommon(userId, friendId);
    }
}

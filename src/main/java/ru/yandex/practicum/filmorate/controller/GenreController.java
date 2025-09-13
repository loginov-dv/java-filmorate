package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {
    // Сервис по работе с жанрами
    private final GenreService genreService;
    // Логгер
    private static final Logger logger = LoggerFactory.getLogger(GenreController.class);

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    // Эндпоинт GET /genres
    @GetMapping
    public List<Genre> getAll() {
        logger.debug("Вызов эндпоинта GET /genres");
        return genreService.getAll();
    }

    // Эндпоинт GET /genres/{id}
    @GetMapping("/{id}")
    public Genre getById(@PathVariable int id) {
        logger.debug("Вызов эндпоинта GET /genres/{id}");
        return genreService.getById(id);
    }
}

package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.NewDirectorRequest;
import ru.yandex.practicum.filmorate.dto.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;
    private static final Logger logger = LoggerFactory.getLogger(DirectorController.class);

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    // Эндпоинт GET /directors
    @GetMapping
    public List<Director> getAll() {
        logger.debug("Вызов эндпоинта GET /directors");
        return directorService.getAll();
    }

    // Эндпоинт GET /directors/{id}
    @GetMapping("/{id}")
    public Director getById(@PathVariable @Positive int id) {
        logger.debug("Вызов эндпоинта GET /directors/{id}");
        return directorService.getById(id);
    }

    // Эндпоинт POST /directors
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Director create(@Valid @RequestBody NewDirectorRequest request) {
        logger.debug("Вызов эндпоинта POST /directors");
        return directorService.create(request);
    }

    // Эндпоинт PUT /directors
    @PutMapping
    public Director update(@Valid @RequestBody UpdateDirectorRequest request) {
        logger.debug("Вызов эндпоинта PUT /directors");
        return directorService.update(request);
    }

    // Эндпоинт DELETE /directors/{id}
    @DeleteMapping("/{id}")
    public void delete(@PathVariable @Positive int id) {
        logger.debug("Вызов эндпоинта DELETE /directors/{id}");
        directorService.removeById(id);
    }
}

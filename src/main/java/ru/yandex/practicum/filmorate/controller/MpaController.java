package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;
    private static final Logger logger = LoggerFactory.getLogger(MpaController.class);

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    // Эндпоинт GET /mpa
    @GetMapping
    public List<MpaRating> getAll() {
        logger.debug("Вызов эндпоинта GET /mpa");
        return mpaService.getAll();
    }

    // Эндпоинт GET /mpa/{id}
    @GetMapping("/{id}")
    public MpaRating getById(@PathVariable @Positive int id) {
        logger.debug("Вызов эндпоинта GET /mpa/{id}");
        return mpaService.getById(id);
    }
}

package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

// Сервис по работе с жанрами
@Service
public class GenreService {
    // Репозиторий жанров
    private final GenreRepository genreRepository;
    // Логгер
    private static final Logger logger = LoggerFactory.getLogger(GenreService.class);

    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    // Вернуть все жанры
    public List<Genre> getAll() {
        logger.debug("Запрос на получение всех жанров");
        return genreRepository.getAll();
    }

    // Вернуть жанр по id
    public Genre getById(int id) {
        logger.debug("Запрос на получение жанра с id = {}", id);

        Optional<Genre> maybeGenre = genreRepository.getById(id);

        if (maybeGenre.isEmpty()) {
            logger.warn("Жанр с id = {} не найден", id);
            throw new NotFoundException("Жанр с id = " + id + " не найден");
        }

        return maybeGenre.get();
    }
}

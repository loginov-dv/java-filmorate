package ru.yandex.practicum.filmorate.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.*;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

// Сервис по работе с фильмами
@Service
public class FilmService {
    // Репозиторий фильмов
    private final FilmRepository filmRepository;
    // Логгер
    private static final Logger logger = LoggerFactory.getLogger(FilmService.class);
    // Репозиторий рейтингов
    private final MpaRepository mpaRepository;
    // Репозиторий жанров
    private final GenreRepository genreRepository;
    // Репозиторий пользователей
    private final UserRepository userRepository;
    // Репозиторий режиссёров
    private final DirectorRepository directorRepository;

    private static final int MIN_RELEASE_YEAR = 1895;

    @Autowired
    public FilmService(FilmRepository filmRepository, GenreRepository genreRepository,
                       MpaRepository mpaRepository, UserRepository userRepository,
                       DirectorRepository directorRepository) {
        this.filmRepository = filmRepository;
        this.genreRepository = genreRepository;
        this.mpaRepository = mpaRepository;
        this.userRepository = userRepository;
        this.directorRepository = directorRepository;
    }

    // Вернуть все фильмы
    public List<FilmDto> getAll() {
        logger.debug("Запрос на получение всех фильмов");

        return filmRepository.getAll().stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    // Вернуть фильм по id
    public FilmDto getById(int id) {
        logger.debug("Запрос на получение фильма с id = {}", id);

        Optional<Film> maybeFilm = filmRepository.getById(id);

        if (maybeFilm.isEmpty()) {
            logger.warn("Фильм с id = {} не найден", id);
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }

        return FilmMapper.mapToFilmDto(maybeFilm.get());
    }

    // Создать новый фильм
    public FilmDto create(NewFilmRequest request) {
        logger.debug("Запрос на создания нового фильма");
        logger.debug("Входные данные: {}", request);

        Optional<MpaRating> maybeRating = mpaRepository.getById(request.getMpa().getId());

        if (maybeRating.isEmpty()) {
            logger.warn("Рейтинг с id = {} не найден", request.getMpa().getId());
            throw new NotFoundException("Рейтинг с id = " + request.getMpa().getId() + " не найден");
        }

        MpaRating mpaRating = maybeRating.get();

        Set<Genre> genres = new HashSet<>();
        if (request.getGenres() != null) {
            for (GenreIdDto genreIdDto : request.getGenres()) {
                Optional<Genre> maybeGenre = genreRepository.getById(genreIdDto.getId());

                if (maybeGenre.isEmpty()) {
                    logger.warn("Жанр с id = {} не найден", genreIdDto.getId());
                    throw new NotFoundException("Жанр с id = " + genreIdDto.getId() + " не найден");
                }

                genres.add(maybeGenre.get());
            }
        }

        Set<Director> directors = new HashSet<>();
        if (request.getDirectors() != null) {
            for (DirectorIdDto directorIdDto : request.getDirectors()) {
                Optional<Director> maybeDirector = directorRepository.getById(directorIdDto.getId());

                if (maybeDirector.isEmpty()) {
                    logger.warn("Режиссёр с id = {} не найден", directorIdDto.getId());
                    throw new NotFoundException("Режиссёр с id = " + directorIdDto.getId() + " не найден");
                }

                directors.add(maybeDirector.get());
            }
        }

        Film film = FilmMapper.mapToFilm(request, mpaRating, genres, directors);
        filmRepository.create(film);

        logger.info("Создан фильм: {}", film);
        return FilmMapper.mapToFilmDto(film);
    }

    // Изменить фильм
    public FilmDto update(UpdateFilmRequest request) {
        logger.debug("Запрос на изменение фильма с id = {}", request.getId());
        logger.debug("Входные данные: {}", request);

        Optional<Film> maybeFilm = filmRepository.getById(request.getId());

        if (maybeFilm.isEmpty()) {
            logger.warn("Фильм с id = {} не найден", request.getId());
            throw new NotFoundException("Фильм с id = " + request.getId() + " не найден");
        }

        Set<Director> directors = new HashSet<>();
        if (request.hasDirectors()) {
            for (DirectorIdDto directorIdDto : request.getDirectors()) {
                Optional<Director> maybeDirector = directorRepository.getById(directorIdDto.getId());

                if (maybeDirector.isEmpty()) {
                    logger.warn("Режиссёр с id = {} не найден", directorIdDto.getId());
                    throw new NotFoundException("Режиссёр с id = " + directorIdDto.getId() + " не найден");
                }

                directors.add(maybeDirector.get());
            }
        }

        logger.debug("Исходное состояние: {}", maybeFilm.get());
        Film updatedFilm = FilmMapper.updateFilmFields(maybeFilm.get(), request, directors);
        updatedFilm = filmRepository.update(updatedFilm);

        logger.info("Изменён фильм: {}", updatedFilm);
        return FilmMapper.mapToFilmDto(updatedFilm);
    }


    // Поставить лайк
    public void putLike(int filmId, int userId) {
        logger.debug("Запрос на добавление лайка фильма с id = {} от пользователя с id = {}",
                filmId, userId);

        if (filmRepository.getById(filmId).isEmpty()) {
            logger.warn("Фильм с id = {} не найден", filmId);
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }

        if (userRepository.getById(userId).isEmpty()) {
            logger.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        if (filmRepository.getLikesUserId(filmId).contains(userId)) {
            logger.warn("Пользователь с id = {} уже поставил лайк фильму с id = {}", userId, filmId);
            throw new ValidationException("Пользователь с id = " + userId +
                    " уже поставил лайк фильму с id = " + filmId);
        }

        filmRepository.putLike(filmId, userId);
        logger.info("Пользователь с id = {} поставил лайк фильму с id = {}", userId, filmId);
    }

    // Удалить лайк
    @Transactional
    public void removeLike(int filmId, int userId) {
        logger.debug("Запрос на удаление лайка фильма с id = {} от пользователя с id = {}",
                filmId, userId);

        if (filmRepository.getById(filmId).isEmpty()) {
            logger.warn("Фильм с id = {} не найден", filmId);
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }

        if (userRepository.getById(userId).isEmpty()) {
            logger.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        filmRepository.removeLike(filmId, userId);
        logger.info("Пользователь с id = {} убрал лайк у фильма с id = {}", userId, filmId);
    }

    // Полуить список из первых count фильмов по количеству лайков
    public List<FilmDto> getPopular(int count, Integer genreId, Integer year) {
        logger.debug("Запрос на получение первых {} популярных фильмов", count);

        if (count <= 0) {
            logger.warn("Количество фильмов должно быть положительным числом");
            throw new ValidationException("Количество фильмов должно быть положительным числом");
        }

        if (genreId != null && genreId < 1) {
            logger.warn("ID жанра должен быть положительным");
            throw new ValidationException("ID жанра должен быть положительным");
        }
        if (year != null && year < MIN_RELEASE_YEAR) {
            logger.warn("Год должен быть не ранее {}", MIN_RELEASE_YEAR);
            throw new ValidationException("Год должен быть не ранее " + MIN_RELEASE_YEAR);
        }

        if (genreId != null && genreRepository.getById(genreId).isEmpty()) {
            logger.warn("Жанр с id {} не найден", genreId);
            throw new ValidationException("Жанр с id " + genreId + " не найден");
        }

        List<Film> popular = filmRepository.getPopular(count, genreId, year);

        logger.info("Популярные фильмы: {}", popular.stream()
                .map(Film::getId)
                .collect(Collectors.toList()));
        return popular.stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }
  
    // Удалить фильм по id
    public void removeFilmById(int filmId) {
        filmRepository.removeFilmById(filmId);
    }
  
    public List<FilmDto> search(int directorId, String sortBy) {
        Optional<Director> maybeDirector = directorRepository.getById(directorId);

        if (maybeDirector.isEmpty()) {
            logger.warn("Режиссёр с id = {} не найден", directorId);
            throw new NotFoundException("Режиссёр с id = " + directorId + " не найден");
        }

        List<Film> searchResult;
        if (sortBy.equals("year")) {
            searchResult = filmRepository.searchDirectorsFilmsSortedByYear(directorId);
        } else if (sortBy.equals("likes")) {
            searchResult = filmRepository.searchDirectorsFilmsSortedByLikes(directorId);
        } else {
            logger.warn("Переданный параметр сортировки sortBy = {} не поддерживается", sortBy);
            throw new NotFoundException("Переданный параметр сортировки sortBy = " + sortBy + " не поддерживается");
        }

        logger.info("Найденные фильмы: {}", searchResult.stream()
                .map(Film::getId)
                .collect(Collectors.toList()));
        return searchResult.stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }
}
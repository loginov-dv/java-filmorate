package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.GenreIdDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
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

    @Autowired
    public FilmService(FilmRepository filmRepository, GenreRepository genreRepository,
                       MpaRepository mpaRepository, UserRepository userRepository) {
        this.filmRepository = filmRepository;
        this.genreRepository = genreRepository;
        this.mpaRepository = mpaRepository;
        this.userRepository = userRepository;
    }

    // Вернуть все фильмы
    public List<FilmDto> getAll() {
        logger.debug("Запрос на получение всех фильмов");

        List<Film> films = filmRepository.getAll().stream()
                .map(this::getMpaAndGenres)
                .collect(Collectors.toList());

        return films.stream()
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

        Film film = getMpaAndGenres(maybeFilm.get());

        return FilmMapper.mapToFilmDto(film);
    }

    // Добавить рейтинг и жанры
    private Film getMpaAndGenres(Film film) {
        Optional<MpaRating> maybeRating = mpaRepository.getById(film.getRating().getId());

        if (maybeRating.isEmpty()) {
            logger.warn("Рейтинг с id = {} не найден", film.getRating().getId());
            throw new NotFoundException("Рейтинг с id = " + film.getRating().getId() + " не найден");
        }

        List<Genre> genres = genreRepository.getByFilmId(film.getId());

        film.setRating(maybeRating.get());
        film.setGenres(new HashSet<>(genres));

        return film;
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

        Film film = FilmMapper.mapToFilm(request, mpaRating, genres);
        filmRepository.create(film);

        logger.info("Создан фильм: {}", film);
        return FilmMapper.mapToFilmDto(film);
    }

    // Изменить фильм
    public FilmDto update(UpdateFilmRequest request) {
        logger.debug("Запрос на изменение фильма с id = {}", request.getId());
        logger.debug("Входные данные: {}", request);

        // TODO: mpa & genres?
        Optional<Film> maybeFilm = filmRepository.getById(request.getId());

        if (maybeFilm.isEmpty()) {
            logger.warn("Фильм с id = {} не найден", request.getId());
            throw new NotFoundException("Фильм с id = " + request.getId() + " не найден");
        }

        logger.debug("Исходное состояние: {}", maybeFilm.get());
        Film updatedFilm = FilmMapper.updateFilmFields(maybeFilm.get(), request);
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
    public List<FilmDto> getPopular(int count) {
        logger.debug("Запрос на получение первых {} популярных фильмов", count);

        if (count <= 0) {
            logger.warn("Количество фильмов должно быть положительным числом");
            throw new ValidationException("Количество фильмов должно быть положительным числом");
        }

        List<Film> popular = filmRepository.getPopular(count);

        logger.info("Популярные фильмы: {}", popular.stream()
                .map(Film::getId)
                .collect(Collectors.toList()));
        return popular.stream()
                .map(this::getMpaAndGenres)
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }
}
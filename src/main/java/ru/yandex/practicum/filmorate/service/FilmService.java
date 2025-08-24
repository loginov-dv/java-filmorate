package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.GenreIdDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
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
    // Сервис по работе с пользователями
    private final UserService userService;
    // Репозиторий рейтингов
    private final MpaRepository mpaRepository;
    // Репозиторий жанров
    private final GenreRepository genreRepository;

    @Autowired
    public FilmService(FilmRepository filmRepository, GenreRepository genreRepository,
                       MpaRepository mpaRepository, UserService userService) {
        this.filmRepository = filmRepository;
        this.genreRepository = genreRepository;
        this.mpaRepository = mpaRepository;
        this.userService = userService;
    }

    // Вернуть все фильмы
    public List<FilmDto> getAll() {
        List<Film> films = filmRepository.getAll();
        for (Film film : films) {
            getMpaAndGenres(film);
        }

        return films.stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    // Вернуть фильм по id
    public FilmDto getById(int id) {
        Optional<Film> maybeFilm = filmRepository.getById(id);

        if (maybeFilm.isEmpty()) {
            logger.warn("Фильм с id = {} не найден", id);
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }

        Film film = getMpaAndGenres(maybeFilm.get());

        return FilmMapper.mapToFilmDto(film);
    }

    private Film getMpaAndGenres(Film film) {
        Optional<MpaRating> maybeRating = mpaRepository.getById(film.getRating().getId());

        // TODO: logs
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
        Optional<MpaRating> maybeRating = mpaRepository.getById(request.getMpa().getId());

        if (maybeRating.isEmpty()) {
            logger.warn("Рейтинг с id = {} не найден", request.getMpa().getId());
            throw new NotFoundException("Рейтинг с id = " + request.getMpa().getId() + " не найден");
        }

        // TODO: null?
        // TODO: duplicates
        Set<Genre> genres = new HashSet<>();
        for (GenreIdDto genreIdDto : request.getGenres()) {
            Optional<Genre> maybeGenre = genreRepository.getById(genreIdDto.getId());

            if (maybeGenre.isEmpty()) {
                logger.warn("Жанр с id = {} не найден", genreIdDto.getId());
                throw new NotFoundException("Жанр с id = " + genreIdDto.getId() + " не найден");
            }

            genres.add(maybeGenre.get());
        }

        MpaRating mpaRating = maybeRating.get();

        Film film = FilmMapper.mapToFilm(request, mpaRating, genres);

        filmRepository.create(film);
        logger.info("Создан фильм: id = {}, name = {}", film.getId(), film.getName());

        return FilmMapper.mapToFilmDto(film);
    }

    // Изменить фильм
    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            logger.warn("Не указан id");
            throw new ValidationException("Не указан id");
        }

        Optional<Film> maybeFilm = filmRepository.getById(newFilm.getId());
        if (maybeFilm.isPresent()) {
            filmRepository.update(newFilm);
            logger.info("Изменён фильм: id = {}, name = {}", newFilm.getId(), newFilm.getName());

            return newFilm;
        }

        logger.warn("Фильм с id = {} не найден", newFilm.getId());
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    /*
    // Поставить лайк
    public void putLike(int filmId, int userId) {
        if (!userService.isPresent(userId)) {
            logger.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (!isPresent(filmId)) {
            logger.warn("Фильм с id = {} не найден", filmId);
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }

        filmStorage.putLike(filmId, userId);
        logger.info("Пользователь с id = {} поставил лайк фильму с id = {}", userId, filmId);
    }

    // Удалить лайк
    public void removeLike(int filmId, int userId) {
        if (!userService.isPresent(userId)) {
            logger.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (!isPresent(filmId)) {
            logger.warn("Фильм с id = {} не найден", filmId);
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }

        filmStorage.removeLike(filmId, userId);
        logger.info("Пользователь с id = {} убрал лайк у фильма с id = {}", userId, filmId);
    }

    // Полуить список из первых count фильмов по количеству лайков
    public Collection<Film> getPopular(int count) {
        if (count <= 0) {
            logger.warn("Количество фильмов должно быть положительным числом");
            throw new ValidationException("Количество фильмов должно быть положительным числом");
        }

        return filmStorage.getAll().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    // Убрать лайки у всех фильмов
    public void clearLikes() {
        filmStorage.clearLikes();
    }

    // Вспомогательный метод для проверки наличия фильма с указанным id
    public boolean isPresent(int id) {
        return filmStorage.getById(id).isPresent();
    }

    // Удалить все фильмы
    public void clear() {
        filmStorage.clear();
    }
    */
}
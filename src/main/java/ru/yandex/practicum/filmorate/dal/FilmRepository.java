package ru.yandex.practicum.filmorate.dal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

// Класс-репозиторий для работы с таблицей "films"
@Repository
public class FilmRepository extends BaseRepository<Film> {
    // Наименование таблицы
    private static final String TABLE_NAME = "films";
    // Запросы
    private static final String FIND_ALL_QUERY = "SELECT * FROM " + TABLE_NAME;
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
    private static final String INSERT_FILM_QUERY = "INSERT INTO " + TABLE_NAME +
            "(name, description, release_date, duration, rating_id) " +
            "VALUES(?, ?, ?, ?, ?)";
    private static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO film_genres(film_id, genre_id) " +
            "VALUES(?, ?)";
    private static final String UPDATE_QUERY = "UPDATE " + TABLE_NAME + " " +
            "SET name = ?, description = ?, release_date = ?, duration = ? WHERE id = ?";
    // Логгер
    private static final Logger logger = LoggerFactory.getLogger(FilmRepository.class);

    @Autowired
    public FilmRepository(JdbcTemplate jdbcTemplate, RowMapper<Film> rowMapper) {
        super(jdbcTemplate, rowMapper);
    }

    public List<Film> getAll() {
        logger.debug("Запрос на получение всех фильмов");
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Film> getById(int filmId) {
        logger.debug("Запрос на получение фильма с id = {}", filmId);
        return findOne(FIND_BY_ID_QUERY, filmId);
    }

    public Film create(Film film) {
        int id = insert(INSERT_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRating().getId()
        );
        film.setId(id);

        logger.debug("Добавлен новый фильм: id = {}, name = {}, description = {}, release_date = {}, duration = {}, " +
                        "rating_id = {}", film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getRating().getId());

        for (Genre genre : film.getGenres()) {
            insert(INSERT_FILM_GENRE_QUERY, film.getId(), genre.getId());
            logger.debug("Добавлен жанр с id = {} для фильма с id = {}", genre.getId(), film.getId());
        }

        return film;
    }

    public Film update(Film film) {
        update(UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId());

        logger.debug("Обновлен фильм с id = {}", film.getId());

        return film;
    }
}

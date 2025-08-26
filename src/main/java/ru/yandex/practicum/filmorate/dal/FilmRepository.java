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
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM " + TABLE_NAME + " WHERE film_id = ?";
    private static final String INSERT_FILM_QUERY = "INSERT INTO " + TABLE_NAME +
            "(name, description, release_date, duration, rating_id) " +
            "VALUES(?, ?, ?, ?, ?)";
    private static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO film_genres(film_id, genre_id) " +
            "VALUES(?, ?)";
    private static final String UPDATE_QUERY = "UPDATE " + TABLE_NAME + " " +
            "SET name = ?, description = ?, release_date = ?, duration = ? WHERE film_id = ?";
    private static final String INSERT_FILM_LIKES_QUERY = "INSERT INTO film_likes(film_id, genre_id) " +
            "VALUES(?, ?)";
    private static final String DELETE_FROM_FILM_LIKES_QUERY = "DELETE FROM film_likes " +
            "WHERE film_id = ? AND user_id = ?";
    private static final String GET_POPULAR_QUERY = "SELECT f.name, f.description, f.release_date, f.duration, " +
            "f.rating_id FROM " + TABLE_NAME + " AS f JOIN film_likes AS fl ON f.film_id = fl.film_id " +
            "GROUP BY f.film_id ORDER BY COUNT(fl.user_id) DESC LIMIT ?";
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

    public void putLike(int filmId, int userId) {
        insert(INSERT_FILM_LIKES_QUERY, filmId, userId);
        logger.debug("Добавлена запись в film_likes: film_id = {}, user_id = {}", filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        update(DELETE_FROM_FILM_LIKES_QUERY, filmId, userId);
        logger.debug("Удалена запись из film_likes: film_id = {}, user_id = {}", filmId, userId);
    }

    public List<Film> getPopular(int count) {
        logger.debug("Запрос на получение первых {} популярных фильмов", count);
        return findMany(GET_POPULAR_QUERY, count);
    }
}

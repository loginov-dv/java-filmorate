package ru.yandex.practicum.filmorate.dal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmResultSetExtractor;
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
    private static final String FIND_ALL_QUERY = """
            SELECT
                f.film_id AS film_id,
                f.name AS film_name,
                f.description AS film_description,
                f.release_date AS film_release_date,
                f.duration AS film_duration,
                r.rating_id AS rating_id,
                r.name AS rating_name,
                g.genre_id AS genre_id,
                g.name AS genre_name
            FROM films AS f
            LEFT JOIN ratings AS r ON f.rating_id = r.rating_id
            LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id
            LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
            ORDER BY f.film_id""";
    private static final String FIND_BY_ID_QUERY = """
            SELECT
                f.film_id AS film_id,
                f.name AS film_name,
                f.description AS film_description,
                f.release_date AS film_release_date,
                f.duration AS film_duration,
                r.rating_id AS rating_id,
                r.name AS rating_name,
                g.genre_id AS genre_id,
                g.name AS genre_name
            FROM films AS f
            LEFT JOIN ratings AS r ON f.rating_id = r.rating_id
            LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id
            LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
            WHERE f.film_id = ?""";
    private static final String INSERT_FILM_QUERY = "INSERT INTO " + TABLE_NAME +
            "(name, description, release_date, duration, rating_id) " +
            "VALUES(?, ?, ?, ?, ?)";
    private static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO film_genres(film_id, genre_id) " +
            "VALUES(?, ?)";
    private static final String UPDATE_QUERY = "UPDATE " + TABLE_NAME + " " +
            "SET name = ?, description = ?, release_date = ?, duration = ? WHERE film_id = ?";
    private static final String INSERT_FILM_LIKES_QUERY = "INSERT INTO film_likes(film_id, user_id) " +
            "VALUES(?, ?)";
    private static final String DELETE_FROM_FILM_LIKES_QUERY = "DELETE FROM film_likes " +
            "WHERE film_id = ? AND user_id = ?";
    private static final String GET_POPULAR_QUERY = """
            SELECT
                f.film_id AS film_id,
                f.name AS film_name,
                f.description AS film_description,
                f.release_date AS film_release_date,
                f.duration AS film_duration,
                r.rating_id AS rating_id,
                r.name AS rating_name,
                g.genre_id AS genre_id,
                g.name AS genre_name
            FROM films AS f
            LEFT JOIN film_likes AS fl ON f.film_id = fl.film_id
            LEFT JOIN ratings AS r ON f.rating_id = r.rating_id
            LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id
            LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
            GROUP BY film_id, genre_id
            ORDER BY COUNT(fl.user_id) DESC
            LIMIT ?
            """;
    private static final String GET_FILM_LIKES_QUERY = "SELECT user_id FROM film_likes WHERE film_id = ?";
    // Логгер
    private static final Logger logger = LoggerFactory.getLogger(FilmRepository.class);
    // ResultSetExtractor
    private final FilmResultSetExtractor filmResultSetExtractor;

    @Autowired
    public FilmRepository(JdbcTemplate jdbcTemplate, RowMapper<Film> rowMapper,
                          FilmResultSetExtractor filmResultSetExtractor) {
        super(jdbcTemplate, rowMapper);
        this.filmResultSetExtractor = filmResultSetExtractor;
    }

    public List<Film> getAll() {
        logger.debug("Запрос на получение всех строк таблицы films");
        return findMany(FIND_ALL_QUERY, filmResultSetExtractor);
    }

    public Optional<Film> getById(int filmId) {
        logger.debug("Запрос на получение строки таблицы films с id = {}", filmId);
        return findOne(FIND_BY_ID_QUERY, filmResultSetExtractor, filmId);
    }

    public Film create(Film film) {
        logger.debug("Запрос на вставку в таблицу films");
        int id = insert(INSERT_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRating().getId()
        );
        logger.debug("Получен новый id = {}", id);
        film.setId(id);

        for (Genre genre : film.getGenres()) {
            insert(INSERT_FILM_GENRE_QUERY, film.getId(), genre.getId());
            logger.debug("Добавлена строка в таблицу film_genres: film_id = {}, genre_id = {}",
                    film.getId(), genre.getId());
        }

        logger.debug("Добавлена строка в таблицу films с id = {}", id);
        return film;
    }

    public Film update(Film film) {
        logger.debug("Запрос на обновление строки в таблице films с id = {}", film.getId());
        update(UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId());

        logger.debug("Обновлена строка в таблице films с id = {}", film.getId());
        return film;
    }

    public void putLike(int filmId, int userId) {
        logger.debug("Запрос на вставку строки в таблицу film_likes");
        insert(INSERT_FILM_LIKES_QUERY, filmId, userId);
        logger.debug("Добавлена строка в таблицу film_likes: film_id = {}, user_id = {}", filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        logger.debug("Запрос на удаление строки из таблицы film_likes");
        update(DELETE_FROM_FILM_LIKES_QUERY, filmId, userId);
        logger.debug("Удалена строка из таблицы film_likes: film_id = {}, user_id = {}", filmId, userId);
    }

    public List<Film> getPopular(int count) {
        logger.debug("Запрос на получение первых {} популярных фильмов", count);
        return findMany(GET_POPULAR_QUERY, filmResultSetExtractor, count);
    }

    public List<Integer> getLikesUserId(int filmId) {
        logger.debug("Запрос на получение всех user_id из таблицы film_likes для film_id = {}", filmId);
        return super.findManyInts(GET_FILM_LIKES_QUERY, filmId);
    }
}

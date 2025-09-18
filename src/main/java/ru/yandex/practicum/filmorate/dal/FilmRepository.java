package ru.yandex.practicum.filmorate.dal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Класс-репозиторий для работы с таблицей "films"
@Repository
public class FilmRepository extends BaseRepository<Film> {
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
                    g.name AS genre_name,
                    d.director_id AS director_id,
                    d.name AS director_name
                FROM films AS f
                LEFT JOIN ratings AS r ON f.rating_id = r.rating_id
                LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id
                LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
                LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id
                LEFT JOIN directors AS d ON fd.director_id = d.director_id
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
                g.name AS genre_name,
                d.director_id AS director_id,
                d.name AS director_name
            FROM films AS f
            LEFT JOIN ratings AS r ON f.rating_id = r.rating_id
            LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id
            LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
            LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id
            LEFT JOIN directors AS d ON fd.director_id = d.director_id
            WHERE f.film_id = ?""";
    private static final String INSERT_FILM_QUERY = "INSERT INTO films" +
            "(name, description, release_date, duration, rating_id) " +
            "VALUES(?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films " +
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
                g.name AS genre_name,
                d.director_id AS director_id,
                d.name AS director_name
            FROM films AS f
            JOIN film_likes AS fl ON f.film_id = fl.film_id
            LEFT JOIN ratings AS r ON f.rating_id = r.rating_id
            LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id
            LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
            LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id
            LEFT JOIN directors AS d ON fd.director_id = d.director_id
            GROUP BY film_id, genre_id, director_id
            ORDER BY COUNT(fl.user_id) DESC
            LIMIT ?
            """;
    private static final String GET_FILM_LIKES_QUERY = "SELECT user_id FROM film_likes WHERE film_id = ?";
    private static final String GET_FILM_DIRECTORS_QUERY = "SELECT director_id FROM film_directors " +
            "WHERE film_id = ?";
    private static final String GET_DIRECTORS_FILMS_ORDERED_BY_YEAR = """
            SELECT
                f.film_id AS film_id,
                f.name AS film_name,
                f.description AS film_description,
                f.release_date AS film_release_date,
                f.duration AS film_duration,
                r.rating_id AS rating_id,
                r.name AS rating_name,
                g.genre_id AS genre_id,
                g.name AS genre_name,
                d.director_id AS director_id,
                d.name AS director_name
            FROM directors AS d
            JOIN film_directors AS fd ON d.director_id = fd.director_id
            JOIN films AS f ON fd.film_id = f.film_id
            LEFT JOIN ratings AS r ON f.rating_id = r.rating_id
            LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id
            LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
            WHERE d.director_id = ?
            GROUP BY film_id, genre_id, director_id
            ORDER BY EXTRACT(YEAR FROM CAST(f.release_date AS date))
            """;
    private static final String GET_DIRECTORS_FILMS_ORDERED_BY_LIKES = """
            SELECT
                f.film_id AS film_id,
                f.name AS film_name,
                f.description AS film_description,
                f.release_date AS film_release_date,
                f.duration AS film_duration,
                r.rating_id AS rating_id,
                r.name AS rating_name,
                g.genre_id AS genre_id,
                g.name AS genre_name,
                d.director_id AS director_id,
                d.name AS director_name
            FROM directors AS d
            JOIN film_directors AS fd ON d.director_id = fd.director_id
            JOIN films AS f ON fd.film_id = f.film_id
            LEFT JOIN film_likes AS fl ON f.film_id = fl.film_id
            LEFT JOIN ratings AS r ON f.rating_id = r.rating_id
            LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id
            LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
            WHERE d.director_id = ?
            GROUP BY film_id, genre_id, director_id
            ORDER BY COUNT(fl.user_id) DESC
            """;

    private static final String SEARCH_BY_TITLE_OR_DIRECTOR_QUERY = """
            WITH likes AS (
                SELECT film_id, COUNT(*) AS cnt
                FROM film_likes
                GROUP BY film_id
            )
            SELECT
                f.film_id           AS film_id,
                f.name              AS film_name,
                f.description       AS film_description,
                f.release_date      AS film_release_date,
                f.duration          AS film_duration,
                r.rating_id         AS rating_id,
                r.name              AS rating_name,
                g.genre_id          AS genre_id,
                g.name              AS genre_name,
                d.director_id       AS director_id,
                d.name              AS director_name,
                COALESCE(l.cnt, 0)  AS likes_cnt
            FROM films AS f
            LEFT JOIN ratings AS r         ON f.rating_id = r.rating_id
            LEFT JOIN film_genres AS fg    ON f.film_id  = fg.film_id
            LEFT JOIN genres AS g          ON fg.genre_id = g.genre_id
            LEFT JOIN film_directors AS fd ON f.film_id  = fd.film_id
            LEFT JOIN directors AS d       ON fd.director_id = d.director_id
            LEFT JOIN likes AS l           ON l.film_id  = f.film_id
            WHERE ( :titleCond ) OR ( :directorCond )
            ORDER BY COALESCE(l.cnt, 0) DESC, f.film_id
            """;

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

        if (!film.getGenres().isEmpty()) {
            List<String> genreValues = film.getGenres().stream()
                    .map(genre -> "(" + film.getId() + ", " + genre.getId() + ")")
                    .toList();
            insertWithoutKey("INSERT INTO film_genres(film_id, genre_id) VALUES "
                    + String.join(", ", genreValues));
            logger.debug("Добавлены строки в таблицу film_genres: film_id = {}, genre_id = {}",
                    film.getId(), film.getGenres().stream().map(Genre::getId).toList());
        }

        if (!film.getDirectors().isEmpty()) {
            List<String> directorValues = film.getDirectors().stream()
                    .map(director -> "(" + film.getId() + ", " + director.getId() + ")")
                    .toList();
            insertWithoutKey("INSERT INTO film_directors(film_id, director_id) VALUES "
                    + String.join(", ", directorValues));
            logger.debug("Добавлены строки в таблицу film_genres: film_id = {}, genre_id = {}",
                    film.getId(), film.getDirectors().stream().map(Director::getId).toList());
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

        List<Integer> oldDirectors = super.findManyInts(GET_FILM_DIRECTORS_QUERY, film.getId());
        List<Integer> newDirectors = film.getDirectors().stream().map(Director::getId).collect(Collectors.toList());

        List<Integer> directorsToRemove = oldDirectors.stream()
                .filter(item -> !newDirectors.contains(item))
                .collect(Collectors.toList());
        List<Integer> directorsToAdd = newDirectors.stream()
                .filter(item -> !oldDirectors.contains(item))
                .collect(Collectors.toList());

        if (!directorsToRemove.isEmpty()) {
            String query = "DELETE FROM film_directors WHERE film_id = ? " +
                    "AND director_id IN (" + createPlaceholders(directorsToRemove.size()) + ")";
            List<Integer> params = new ArrayList<>();
            params.add(film.getId());
            params.addAll(directorsToRemove);

            update(query, params);
            logger.debug("Удалены строки из таблицы film_directors, где film_id = {} и directors_id = {}",
                    film.getId(), directorsToRemove);
        }

        if (!directorsToAdd.isEmpty()) {
            List<String> directorValues = directorsToAdd.stream()
                    .map(directorId -> "(" + film.getId() + ", " + directorId + ")")
                    .toList();
            insertWithoutKey("INSERT INTO film_directors(film_id, director_id) VALUES "
                    + String.join(", ", directorValues));

            logger.debug("Добавлены строки в таблицу film_directors, где film_id = {} и directors_id = {}",
                    film.getId(), directorsToAdd);
        }

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

    public List<Film> searchDirectorsFilmsSortedByYear(int directorId) {
        logger.debug("Запрос на получение всех фильмов режиссёра с id = {}, отсортированных по годам", directorId);
        return findMany(GET_DIRECTORS_FILMS_ORDERED_BY_YEAR, filmResultSetExtractor, directorId);
    }

    public List<Film> searchDirectorsFilmsSortedByLikes(int directorId) {
        logger.debug("Запрос на получение всех фильмов режиссёра с id = {}, отсортированных по лайкам", directorId);
        return findMany(GET_DIRECTORS_FILMS_ORDERED_BY_LIKES, filmResultSetExtractor, directorId);
    }

    // Сортировка по популярности, фильмы с 0 лайков не теряются.
    public List<Film> searchByTitleAndOrDirector(String like, boolean byTitle, boolean byDirector) {
        logger.debug("Поиск фильмов: like='{}', byTitle={}, byDirector={}", like, byTitle, byDirector);

        String titleCond = byTitle ? "LOWER(f.name) LIKE ?" : "1=0";
        String directorCond = byDirector
                ? "EXISTS (SELECT 1 FROM film_directors fdx " +
                "JOIN directors dx ON dx.director_id = fdx.director_id " +
                "WHERE fdx.film_id = f.film_id AND LOWER(dx.name) LIKE ?)"
                : "1=0";

        String sql = SEARCH_BY_TITLE_OR_DIRECTOR_QUERY
                .replace(":titleCond", titleCond)
                .replace(":directorCond", directorCond);

        Object[] params;
        if (byTitle && byDirector) {
            params = new Object[]{ like, like };
        } else if (byTitle) {
            params = new Object[]{ like };
        } else {
            params = new Object[]{ like };
        }

        return findMany(sql, filmResultSetExtractor, params);
    }

    private String createPlaceholders(int count) {
        return String.join(",", Collections.nCopies(count, "?"));
    }
}

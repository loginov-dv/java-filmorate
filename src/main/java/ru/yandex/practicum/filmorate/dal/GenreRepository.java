package ru.yandex.practicum.filmorate.dal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

// Класс-репозиторий для работы с таблицей "genres"
@Repository
public class GenreRepository extends BaseRepository<Genre> {
    // Наименование таблиц
    private static final String TABLE_NAME_GENRES = "genres";
    private static final String TABLE_NAME_FILM_GENRES = "film_genres";
    // Запросы
    private static final String FIND_ALL_QUERY = "SELECT * FROM " + TABLE_NAME_GENRES;
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM " + TABLE_NAME_GENRES + " WHERE id = ?";
    private static final String FIND_BY_FILM_ID_QUERY = "SELECT g.genre_id, g.name FROM " + TABLE_NAME_GENRES + " AS g " +
            "JOIN " + TABLE_NAME_FILM_GENRES + " AS fg ON g.genre_id = fg.genre_id WHERE fg.film_id = ?";
    // Логгер
    private static final Logger logger = LoggerFactory.getLogger(GenreRepository.class);

    @Autowired
    public GenreRepository(JdbcTemplate jdbcTemplate, RowMapper<Genre> rowMapper) {
        super(jdbcTemplate, rowMapper);
    }

    public List<Genre> getAll() {
        logger.debug("Запрос на получение всех жанров");
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Genre> getById(int genreId) {
        logger.debug("Запрос на получение жанра с id = {}", genreId);
        return findOne(FIND_BY_ID_QUERY, genreId);
    }

    public List<Genre> getByFilmId(int filmId) {
        logger.debug("Запрос на получение жанров фильма с id = {}", filmId);
        return findMany(FIND_BY_FILM_ID_QUERY, filmId);
    }
}

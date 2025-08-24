package ru.yandex.practicum.filmorate.dal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

// Класс-репозиторий для работы с таблицей "films"
@Repository
public class FilmRepository extends BaseRepository<Film> {
    // Наименование таблицы
    private static final String TABLE_NAME = "films";
    // Запросы
    private static final String FIND_ALL_QUERY = "SELECT * FROM " + TABLE_NAME;
    private static final String FIND_ALL_IDS_QUERY = "SELECT film_id FROM " + TABLE_NAME;
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
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
}

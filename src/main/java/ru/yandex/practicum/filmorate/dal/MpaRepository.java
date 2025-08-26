package ru.yandex.practicum.filmorate.dal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;
import java.util.Optional;

// Класс-репозиторий для работы с таблицей "ratings"
@Repository
public class MpaRepository extends BaseRepository<MpaRating> {
    // Наименование
    private static final String TABLE_NAME = "ratings";
    // Запросы
    private static final String FIND_ALL_QUERY = "SELECT * FROM " + TABLE_NAME;
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM " + TABLE_NAME + " WHERE rating_id = ?";
    // Логгер
    private static final Logger logger = LoggerFactory.getLogger(MpaRepository.class);

    @Autowired
    public MpaRepository(JdbcTemplate jdbcTemplate, RowMapper<MpaRating> rowMapper) {
        super(jdbcTemplate, rowMapper);
    }

    public List<MpaRating> getAll() {
        logger.debug("Запрос на получение всех строк таблицы ratings");
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<MpaRating> getById(int mpaId) {
        logger.debug("Запрос на получение строки таблицы ratings с id = {}", mpaId);
        return findOne(FIND_BY_ID_QUERY, mpaId);
    }
}

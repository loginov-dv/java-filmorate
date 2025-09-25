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

@Repository
public class MpaRepository extends BaseRepository<MpaRating> {
    private static final Logger logger = LoggerFactory.getLogger(MpaRepository.class);
    // Запросы
    private static final String FIND_ALL_QUERY = "SELECT rating_id, name FROM ratings";
    private static final String FIND_BY_ID_QUERY = "SELECT rating_id, name FROM ratings WHERE rating_id = ?";

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

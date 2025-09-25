package ru.yandex.practicum.filmorate.dal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

@Repository
public class DirectorRepository extends BaseRepository<Director> {
    private static final Logger logger = LoggerFactory.getLogger(DirectorRepository.class);
    // Запросы
    private static final String FIND_ALL_QUERY = "SELECT director_id, name FROM directors";
    private static final String FIND_BY_ID_QUERY = "SELECT director_id, name FROM directors WHERE director_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO directors(name) VALUES(?)";
    private static final String UPDATE_QUERY = "UPDATE directors SET name = ? WHERE director_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM directors WHERE director_id = ?";

    @Autowired
    public DirectorRepository(JdbcTemplate jdbcTemplate, RowMapper<Director> rowMapper) {
        super(jdbcTemplate, rowMapper);
    }

    public List<Director> getAll() {
        logger.debug("Запрос на получение всех строк таблицы directors");
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Director> getById(int directorId) {
        logger.debug("Запрос на получение строки таблицы directors с id = {}", directorId);
        return findOne(FIND_BY_ID_QUERY, directorId);
    }

    public Director create(Director director) {
        logger.debug("Запрос на вставку в таблицу directors");
        int id = insert(INSERT_QUERY, director.getName());
        logger.debug("Получен новый id = {}", id);
        director.setId(id);
        logger.debug("Добавлена строка в таблицу directors с id = {}", id);
        return director;
    }

    public Director update(Director director) {
        logger.debug("Запрос на обновление строки в таблице directors с id = {}", director.getId());
        update(UPDATE_QUERY, director.getName(), director.getId());
        logger.debug("Обновлена строка в таблице directors с id = {}", director.getId());
        return director;
    }

    public void removeById(int directorId) {
        logger.debug("Запрос на удаление строки в таблице directors с id = {}", directorId);
        update(DELETE_QUERY, directorId);
        logger.debug("Удалена строка в таблице directors с id = {}", directorId);
    }
}

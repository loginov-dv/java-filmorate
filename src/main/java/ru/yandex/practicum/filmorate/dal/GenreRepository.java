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
    // Наименование
    private static final String TABLE_NAME = "genres";
    // Запросы
    private static final String FIND_ALL_QUERY = "SELECT * FROM " + TABLE_NAME;
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO " + TABLE_NAME + "(name) VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE " + TABLE_NAME + " SET name = ? WHERE id = ?";
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

    public Optional<Genre> getById(int mpaId) {
        logger.debug("Запрос на получение жанра с id = {}", mpaId);
        return findOne(FIND_BY_ID_QUERY, mpaId);
    }

    public Genre create(Genre genre) {
        int id = insert(INSERT_QUERY, genre.getName());
        genre.setId(id);

        logger.debug("Запрос на добавление жанра: id = {}, name = {}", genre.getId(), genre.getName());
        return genre;
    }

    public Genre update(Genre genre) {
        update(UPDATE_QUERY, genre.getName());

        logger.debug("Запрос на обновление жанра: id = {}, name = {}", genre.getId(), genre.getName());
        return genre;
    }
}

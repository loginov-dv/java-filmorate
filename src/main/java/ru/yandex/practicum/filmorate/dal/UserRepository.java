package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

// Класс-репозиторий для работы с таблицей "users"
@Repository
public class UserRepository extends BaseRepository<User> {
    private static final String TABLE_NAME = "users";
    private static final String FIND_ALL_QUERY = "SELECT * FROM " + TABLE_NAME;
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";

    @Autowired
    public UserRepository(JdbcTemplate jdbcTemplate, RowMapper<User> rowMapper) {
        super(jdbcTemplate, rowMapper);
    }

    public List<User> getAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<User> getById(int userId) {
        return findOne(FIND_BY_ID_QUERY, userId);
    }
}

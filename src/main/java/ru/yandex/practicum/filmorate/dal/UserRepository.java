package ru.yandex.practicum.filmorate.dal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

// Класс-репозиторий для работы с таблицей "users"
// TODO: logs
@Repository
public class UserRepository extends BaseRepository<User> {
    // Наименование таблицы
    private static final String TABLE_NAME = "users";
    // Запросы
    private static final String FIND_ALL_QUERY = "SELECT * FROM " + TABLE_NAME;
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM " + TABLE_NAME + " WHERE email = ?";
    private static final String INSERT_QUERY = "INSERT INTO " + TABLE_NAME +
            "(email, login, name, birthday)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE " + TABLE_NAME + " " +
            "SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
    private static final String FIND_FRIENDS_QUERY = "SELECT u.user_id, u.email, u.login, u.name, u.birthday " +
            "FROM friendships AS f JOIN " + TABLE_NAME + " AS u ON f.friend_id = u.user_id " +
            "WHERE f.user_id = ?";
    private static final String INSERT_INTO_FRIENDSHIPS_QUERY = "INSERT INTO friendships(user_id, friend_id, status) " +
            "VALUES(?, ?, true)";
    private static final String DELETE_FROM_FRIENDSHIPS_QUERY = "DELETE FROM friendships " +
            "WHERE user_id = ? AND friend_id = ?";
    // Логгер
    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);

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

    public Optional<User> getByEmail(String email) {
        return findOne(FIND_BY_EMAIL_QUERY, email);
    }

    public User create(User user) {
        int id = insert(INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);

        logger.debug("Inserted into users: id = {}, email = {}, login = {}, name = {}, birthday = {}",
                user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

        return user;
    }

    public User update(User user) {
        update(UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );

        return user;
    }

    public void addFriend(int userId, int friendId) {
        insert(INSERT_INTO_FRIENDSHIPS_QUERY, userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        update(DELETE_FROM_FRIENDSHIPS_QUERY, userId, friendId);
    }

    public List<User> getFriends(int userId) {
        return findMany(FIND_FRIENDS_QUERY, userId);
    }
}

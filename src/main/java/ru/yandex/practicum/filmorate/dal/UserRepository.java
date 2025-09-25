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

@Repository
public class UserRepository extends BaseRepository<User> {
    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);
    // Запросы
    private static final String FIND_ALL_QUERY = """
            SELECT user_id,
                email,
                login,
                name,
                birthday,
            FROM users
            """;
    private static final String FIND_BY_ID_QUERY = """
            SELECT user_id,
                email,
                login,
                name,
                birthday,
            FROM users
            WHERE user_id = ?
            """;
    private static final String FIND_BY_EMAIL_QUERY = """
            SELECT user_id,
                email,
                login,
                name,
                birthday,
            FROM users
            WHERE email = ?
            """;
    private static final String INSERT_QUERY = "INSERT INTO users" +
            "(email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users " +
            "SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
    private static final String FIND_FRIENDS_QUERY = """
            SELECT u.user_id,
                u.email, 
                u.login, 
                u.name, 
                u.birthday
            FROM friendships AS f JOIN users AS u ON f.friend_id = u.user_id
            WHERE f.user_id = ?
            """;
    private static final String INSERT_INTO_FRIENDSHIPS_QUERY = "INSERT INTO friendships(user_id, friend_id, status) " +
            "VALUES(?, ?, true)";
    private static final String DELETE_FROM_FRIENDSHIPS_QUERY = "DELETE FROM friendships " +
            "WHERE user_id = ? AND friend_id = ?";
    private static final String DELETE_USER_QUERY = "DELETE FROM users WHERE user_id = ?";

    @Autowired
    public UserRepository(JdbcTemplate jdbcTemplate, RowMapper<User> rowMapper) {
        super(jdbcTemplate, rowMapper);
    }

    public List<User> getAll() {
        logger.debug("Запрос на получение всех строк таблицы users");
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<User> getById(int userId) {
        logger.debug("Запрос на получение строки таблицы users с id = {}", userId);
        return findOne(FIND_BY_ID_QUERY, userId);
    }

    public Optional<User> getByEmail(String email) {
        logger.debug("Запрос на получение строки таблицы users с email = {}", email);
        return findOne(FIND_BY_EMAIL_QUERY, email);
    }

    public User create(User user) {
        logger.debug("Запрос на вставку в таблицу users");
        int id = insert(INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        logger.debug("Получен новый id = {}", id);
        user.setId(id);

        logger.debug("Добавлена строка в таблицу users с id = {}", id);
        return user;
    }

    public User update(User user) {
        logger.debug("Запрос на обновление строки в таблице users с id = {}", user.getId());
        update(UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );

        logger.debug("Обновлена строка в таблице users с id = {}", user.getId());
        return user;
    }

    public void addFriend(int userId, int friendId) {
        logger.debug("Запрос на вставку строки в таблицу friendships");
        insert(INSERT_INTO_FRIENDSHIPS_QUERY, userId, friendId);
        logger.debug("Добавлена строка в таблицу friendships: user_id = {}, friend_id = {}", userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        logger.debug("Запрос на удаление строки из таблицы friendships");
        update(DELETE_FROM_FRIENDSHIPS_QUERY, userId, friendId);
        logger.debug("Удалена строка из таблицы friendships: user_id = {}, friend_id = {}", userId, friendId);
    }

    public List<User> getFriends(int userId) {
        logger.debug("Запрос на получение всех друзей пользователя с id = {}", userId);
        return findMany(FIND_FRIENDS_QUERY, userId);
    }

    public void removeUserById(int userId) {
        logger.debug("Запрос на удаление пользователя с user_id = {}", userId);
        update(DELETE_USER_QUERY, userId);
    }
}

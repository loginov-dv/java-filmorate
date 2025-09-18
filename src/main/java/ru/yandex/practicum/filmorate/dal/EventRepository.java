package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.events.Event;

import java.sql.Timestamp;
import java.util.List;

// Репозиторий для событий
@Slf4j
@Repository
public class EventRepository extends BaseRepository<Event> {
    // Запросы
    private static final String INSERT_QUERY = "INSERT INTO events(user_id, timestamp, entity_id, type, operation) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String GET_FEED_BY_USER_ID = """
            SElECT event_id,
                timestamp,
                user_id,
                entity_id,
                type,
                operation
            FROM events
            WHERE user_id = ?
            """;

    @Autowired
    public EventRepository(JdbcTemplate jdbcTemplate, RowMapper<Event> rowMapper) {
        super(jdbcTemplate, rowMapper);
    }

    // Создать новое событие
    public void create(Event event) {
        log.debug("Запрос на вставку в таблицу events");
        insertWithoutKey(INSERT_QUERY,
                event.getUserId(),
                Timestamp.from(event.getTimestamp()),
                event.getEntityId(),
                event.getEventType(),
                event.getOperation()
        );
        log.debug("Добавлена строка в таблицу event");
    }

    // Получить все события для пользователя с указанным id
    public List<Event> getUsersFeed(int userId) {
        log.debug("Запрос на получение всех строк таблицы event, где user_id = {}", userId);
        return findMany(GET_FEED_BY_USER_ID, userId);
    }
}

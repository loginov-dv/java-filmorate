package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.events.Event;
import ru.yandex.practicum.filmorate.model.events.EventType;
import ru.yandex.practicum.filmorate.model.events.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;

// Имплементация RowMapper для Event
@Component
public class EventRowMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        Event event = new Event();

        event.setEventId(rs.getInt("event_id"));
        event.setTimestamp(rs.getTimestamp("timestamp").toInstant());
        event.setUserId(rs.getInt("user_id"));
        event.setEntityId(rs.getInt("entity_id"));
        event.setEventType(EventType.valueOf(rs.getString("type")));
        event.setOperation(Operation.valueOf(rs.getString("operation")));

        return event;
    }
}

package ru.yandex.practicum.filmorate.model.events;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

// Модель данных для события
@Data
@NoArgsConstructor
public class Event {
    private Integer userId;
    private Integer eventId;
    private Instant timestamp;
    private Integer entityId;
    private EventType eventType;
    private Operation operation;

    public Event(int userId, int entityId, EventType type, Operation operation) {
        this.userId = userId;
        this.entityId = entityId;
        this.eventType = type;
        this.operation = operation;
        timestamp = Instant.now();
    }
}
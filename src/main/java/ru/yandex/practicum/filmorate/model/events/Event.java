package ru.yandex.practicum.filmorate.model.events;

import lombok.Data;

import java.time.Instant;

// Модель данных для события
@Data
public class Event {
    private Integer userId;
    private Integer eventId;
    private Instant timestamp;
    private Integer entityId;
    private EventType eventType;
    private Operation operation;
}
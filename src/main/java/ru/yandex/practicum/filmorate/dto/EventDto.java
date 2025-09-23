package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

// Dto для события
@Data
public class EventDto {
    private Long timestamp;
    private Integer userId;
    private String eventType;
    private String operation;
    private Integer eventId;
    private Integer entityId;
}
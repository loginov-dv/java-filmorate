package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.model.events.Event;

// Маппер для класса Event
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EventMapper {
    // Маппер Event -> EventDto
    public static EventDto mapToEventDto(Event event) {
        EventDto eventDto = new EventDto();

        eventDto.setTimestamp(event.getTimestamp().toEpochMilli());
        eventDto.setUserId(event.getUserId());
        eventDto.setEventType(event.getEventType().name());
        eventDto.setOperation(event.getOperation().name());
        eventDto.setEventId(event.getEventId());
        eventDto.setEntityId(event.getEntityId());

        return eventDto;
    }
}

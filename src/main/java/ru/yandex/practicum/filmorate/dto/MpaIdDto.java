package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

// Dto для MpaRating
@Data
public class MpaIdDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int id;
}

package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

// Dto для Genre
@Data
public class GenreIdDto {
    //@JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int id;
}

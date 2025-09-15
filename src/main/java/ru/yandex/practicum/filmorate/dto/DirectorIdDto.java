package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

// Director dto
@Data
public class DirectorIdDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int id;
}

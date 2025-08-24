package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.MpaIdDto;
import ru.yandex.practicum.filmorate.model.MpaRating;

// Маппер для класса Mpa
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MpaMapper {

    // Преобразовать MpaRating в MpaIdDto
    public static MpaIdDto mapToMpaIdDto(MpaRating mpaRating) {
        MpaIdDto mpaIdDto = new MpaIdDto();
        mpaIdDto.setId(mpaIdDto.getId());

        return mpaIdDto;
    }
}

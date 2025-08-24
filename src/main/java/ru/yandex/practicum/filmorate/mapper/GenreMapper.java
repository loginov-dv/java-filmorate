package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.GenreIdDto;
import ru.yandex.practicum.filmorate.model.Genre;

// Маппер для класса Genre
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GenreMapper {

    // Преобразовать Genre в GenreIdDto
    public static GenreIdDto mapToGenreIdDto(Genre genre) {
        GenreIdDto genreIdDto = new GenreIdDto();
        genreIdDto.setId(genre.getId());

        return genreIdDto;
    }
}

package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.GenreIdDto;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// Маппер для класса Genre
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GenreMapper {

    // Преобразовать Genre в GenreIdDto
    public static GenreIdDto mapToGenreIdDto(Genre genre) {
        GenreIdDto genreIdDto = new GenreIdDto();
        genreIdDto.setId(genre.getId());

        return genreIdDto;
    }

    // Преобразовать коллекцию Genre в коллекцию GenreIdDto
    public static List<GenreIdDto> mapToListOfGenreIdDto(Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return Collections.emptyList();
        }

        return genres.stream()
                .map(GenreMapper::mapToGenreIdDto)
                .collect(Collectors.toList());
    }
}

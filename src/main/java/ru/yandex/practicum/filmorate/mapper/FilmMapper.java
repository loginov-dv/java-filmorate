package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

// Маппер для класса Film
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {
    // Преобразовать Film в FilmDto
    public static FilmDto mapToFilmDto(Film film) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());

        dto.setMpa(film.getRating());

        if (!film.getGenres().isEmpty()) {
            Set<Genre> genres = film.getGenres().stream()
                    .sorted(Comparator.comparingInt(Genre::getId))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            dto.setGenres(genres);
        }

        if (!film.getDirectors().isEmpty()) {
            Set<Director> directors = film.getDirectors().stream()
                    .sorted(Comparator.comparingInt(Director::getId))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            dto.setDirectors(directors);
        }

        return dto;
    }

    // Преобразовать NewFilmRequest в Film
    public static Film mapToFilm(NewFilmRequest request, MpaRating mpaRating,
                                 Set<Genre> genres, Set<Director> directors) {
        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());

        film.setRating(mpaRating);
        film.setGenres(genres);
        film.setDirectors(directors);

        return film;
    }

    // Обновить поля объекта класса Film
    public static Film updateFilmFields(Film film, UpdateFilmRequest request,
                                        Set<Director> directors) {
        if (request.hasName()) {
            film.setName(request.getName());
        }

        if (request.hasDescription()) {
            film.setDescription(request.getDescription());
        }

        if (request.hasReleaseDate()) {
            film.setReleaseDate(request.getReleaseDate());
        }

        if (request.hasDuration()) {
            film.setDuration(request.getDuration());
        }

        if (request.hasDirectors()) {
            film.setDirectors(directors);
        }

        return film;
    }
}

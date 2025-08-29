package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dal.mappers.FilmResultSetExtractor;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@Sql(scripts = { "/schema.sql", "/data.sql", "/test-data.sql" })
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = { FilmRowMapper.class, FilmRepository.class,
        GenreRowMapper.class, GenreRepository.class, FilmResultSetExtractor.class })
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmRepositoryTest {
    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;

    @Test
    void shouldFindFilmById() {
        Optional<Film> maybeFilm = filmRepository.getById(1);

        assertThat(maybeFilm)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1));
    }

    @Test
    @Order(1)
    void shouldFindAll() {
        List<Film> films = filmRepository.getAll();

        assertEquals(5, films.size());
    }

    @Test
    void shouldCreateNewFilm() {
        MpaRating mpa = new MpaRating();
        mpa.setId(1);

        Genre genre = new Genre();
        genre.setId(1);

        Film newFilm = new Film();
        newFilm.setName("new");
        newFilm.setDescription("new");
        newFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        newFilm.setDuration(120);
        newFilm.setRating(mpa);
        newFilm.setGenres(Set.of(genre));

        Film newFilmFromDB = filmRepository.create(newFilm);

        Optional<Film> maybeFilm = filmRepository.getById(newFilmFromDB.getId());

        assertThat(maybeFilm)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", newFilmFromDB.getId()))
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("name", newFilmFromDB.getName()))
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("description", newFilmFromDB.getDescription()))
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("releaseDate", newFilmFromDB.getReleaseDate()))
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("duration", newFilmFromDB.getDuration()));

        assertNotNull(maybeFilm.get().getRating());
        assertEquals(1, maybeFilm.get().getRating().getId());

        List<Genre> filmGenres = genreRepository.getByFilmId(newFilmFromDB.getId());

        assertEquals(1, filmGenres.size());
        assertEquals(1, filmGenres.getFirst().getId());
    }

    @Test
    void shouldUpdateFilm() {
        Film updatedFilm = new Film();
        updatedFilm.setId(1);
        updatedFilm.setName("update");
        updatedFilm.setDescription("update");
        updatedFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        updatedFilm.setDuration(30);

        filmRepository.update(updatedFilm);

        Optional<Film> maybeFilm = filmRepository.getById(updatedFilm.getId());

        assertThat(maybeFilm)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", updatedFilm.getId()))
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("name", updatedFilm.getName()))
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("description", updatedFilm.getDescription()))
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("releaseDate", updatedFilm.getReleaseDate()))
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("duration", updatedFilm.getDuration()));

        assertNotNull(maybeFilm.get().getRating());
        assertEquals(1, maybeFilm.get().getRating().getId());

        List<Genre> filmGenres = genreRepository.getByFilmId(updatedFilm.getId());

        assertEquals(0, filmGenres.size());
    }

    @Test
    void shouldPutLike() {
        filmRepository.putLike(1, 1);
    }

    @Test
    void shouldRemoveLike() {
        filmRepository.putLike(1, 2);
        filmRepository.removeLike(1, 2);
    }

    @Test
    void shouldGetPopular() {
        filmRepository.putLike(2, 1);
        filmRepository.putLike(2, 2);
        filmRepository.putLike(2, 3);
        filmRepository.putLike(3, 1);
        filmRepository.putLike(3, 2);
        filmRepository.putLike(4, 1);

        List<Film> popular = filmRepository.getPopular(3);

        assertEquals(3, popular.size());
        assertIterableEquals(List.of(2, 3, 4), popular.stream()
                .map(Film::getId)
                .collect(Collectors.toList()));
    }
}
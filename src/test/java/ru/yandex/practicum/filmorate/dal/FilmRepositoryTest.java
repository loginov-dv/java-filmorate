package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
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
@Sql(scripts = {"/schema.sql", "/data.sql", "/test-data.sql"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {FilmRowMapper.class, FilmRepository.class,
        GenreRowMapper.class, GenreRepository.class, FilmResultSetExtractor.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmRepositoryTest {
    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;

    @Autowired
    private JdbcTemplate jdbc;

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

    // ======== Helpers ========
    private int filmIdByName(String name) {
        return jdbc.queryForObject("SELECT film_id FROM films WHERE name = ?", Integer.class, name);
    }

    // ======== Тесты поиска в репозитории ========

    @Test
    void repo_searchByTitle_sortedByLikes() {
        // уникальный префикс, чтобы матчились только наши фильмы
        String p = "repocro_";

        jdbc.update("INSERT INTO films(name, description, release_date, duration, rating_id) VALUES (?,?,?,?,1)",
                p + "A", "d", "2000-01-01", 100);
        jdbc.update("INSERT INTO films(name, description, release_date, duration, rating_id) VALUES (?,?,?,?,1)",
                p + "B", "d", "2001-01-01", 100);
        jdbc.update("INSERT INTO films(name, description, release_date, duration, rating_id) VALUES (?,?,?,?,1)",
                p + "C", "d", "2002-01-01", 100);

        int aId = filmIdByName(p + "A");
        int bId = filmIdByName(p + "B");
        int cId = filmIdByName(p + "C");

        // лайки: A=2, B=1, C=0
        jdbc.update("INSERT INTO film_likes(film_id, user_id) VALUES (?,?)", aId, 1);
        jdbc.update("INSERT INTO film_likes(film_id, user_id) VALUES (?,?)", aId, 2);
        jdbc.update("INSERT INTO film_likes(film_id, user_id) VALUES (?,?)", bId, 1);

        List<Film> list = filmRepository.searchByTitleAndOrDirector("%repocro_%", true, false);
        // забираем только наши (на случай, если кто-то ещё совпадёт)
        List<Film> ours = list.stream().filter(f -> f.getName().startsWith(p)).toList();

        assertThat(ours).extracting(Film::getName)
                .containsExactly(p + "A", p + "B", p + "C");
    }

    @Test
    void repo_searchByDirector_onlyMatches_sortedByLikes() {
        jdbc.update("INSERT INTO films(name, description, release_date, duration, rating_id) VALUES (?,?,?,?,1)",
                "RD_X1", "d", "2003-01-01", 100);
        jdbc.update("INSERT INTO films(name, description, release_date, duration, rating_id) VALUES (?,?,?,?,1)",
                "RD_X2", "d", "2004-01-01", 100);

        int id1 = filmIdByName("RD_X1");
        int id2 = filmIdByName("RD_X2");

        jdbc.update("INSERT INTO directors(name) VALUES (?)", "RepoDir Alpha repodir");
        int dirId = jdbc.queryForObject("SELECT director_id FROM directors WHERE name = ?", Integer.class, "RepoDir Alpha repodir");
        jdbc.update("INSERT INTO film_directors(film_id, director_id) VALUES (?,?)", id1, dirId);
        jdbc.update("INSERT INTO film_directors(film_id, director_id) VALUES (?,?)", id2, dirId);

        // лайки: id1=1, id2=0
        jdbc.update("INSERT INTO film_likes(film_id, user_id) VALUES (?,?)", id1, 1);

        List<Film> list = filmRepository.searchByTitleAndOrDirector("%repodir%", false, true);
        List<String> names = list.stream().map(Film::getName).filter(n -> n.startsWith("RD_")).toList();

        assertThat(names).containsExactly("RD_X1", "RD_X2");
    }

    @Test
    void repo_searchByDirectorOrTitle_noDuplicates_andOrder() {
        // 3 фильма под 'repocmb'
        jdbc.update("INSERT INTO films(name, description, release_date, duration, rating_id) VALUES (?,?,?,?,1)",
                "repocmb_both", "d", "2005-01-01", 100);
        jdbc.update("INSERT INTO films(name, description, release_date, duration, rating_id) VALUES (?,?,?,?,1)",
                "repocmb_title_only", "d", "2006-01-01", 100);
        jdbc.update("INSERT INTO films(name, description, release_date, duration, rating_id) VALUES (?,?,?,?,1)",
                "Other_for_repocmb", "d", "2007-01-01", 100);

        int idBoth = filmIdByName("repocmb_both");
        int idTitle = filmIdByName("repocmb_title_only");
        int idDir = filmIdByName("Other_for_repocmb");

        // режиссёры: у двух из них имя содержит 'repocmb'
        jdbc.update("INSERT INTO directors(name) VALUES (?)", "repocmb Director One");
        int d1 = jdbc.queryForObject("SELECT director_id FROM directors WHERE name = ?", Integer.class, "repocmb Director One");
        jdbc.update("INSERT INTO film_directors(film_id, director_id) VALUES (?,?)", idBoth, d1);
        jdbc.update("INSERT INTO film_directors(film_id, director_id) VALUES (?,?)", idDir, d1);

        // лайки: both=3, title=2, dir=1
        jdbc.update("INSERT INTO film_likes(film_id, user_id) VALUES (?,?)", idBoth, 1);
        jdbc.update("INSERT INTO film_likes(film_id, user_id) VALUES (?,?)", idBoth, 2);
        jdbc.update("INSERT INTO film_likes(film_id, user_id) VALUES (?,?)", idBoth, 3);

        jdbc.update("INSERT INTO film_likes(film_id, user_id) VALUES (?,?)", idTitle, 1);
        jdbc.update("INSERT INTO film_likes(film_id, user_id) VALUES (?,?)", idTitle, 2);

        jdbc.update("INSERT INTO film_likes(film_id, user_id) VALUES (?,?)", idDir, 1);

        List<Film> list = filmRepository.searchByTitleAndOrDirector("%repocmb%", true, true);
        List<Film> ours = list.stream().filter(f ->
                f.getName().equals("repocmb_both")
                        || f.getName().equals("repocmb_title_only")
                        || f.getName().equals("Other_for_repocmb")
        ).toList();

        assertThat(ours).extracting(Film::getName)
                .containsExactly("repocmb_both", "repocmb_title_only", "Other_for_repocmb");
    }

    // ======== Тесты новых возможностей репозитория (удаление, популярное с фильтрами) ========

    @Test
    void shouldRemoveFilmById() {
        MpaRating mpa = new MpaRating();
        mpa.setId(1);

        Genre genre = new Genre();
        genre.setId(1);

        Film newFilm = new Film();
        newFilm.setName("to-delete");
        newFilm.setDescription("to-delete");
        newFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        newFilm.setDuration(120);
        newFilm.setRating(mpa);
        newFilm.setGenres(Set.of(genre));

        Film created = filmRepository.create(newFilm);
        filmRepository.removeFilmById(created.getId());

        assertThat(filmRepository.getById(created.getId())).isEmpty();
    }

    @Test
    void shouldGetPopularWithGenre() {
        MpaRating mpa = new MpaRating();
        mpa.setId(1);

        Genre genre1 = new Genre();
        genre1.setId(1);

        Film filmWithGenre = new Film();
        filmWithGenre.setName("genre-film");
        filmWithGenre.setDescription("genre-film");
        filmWithGenre.setReleaseDate(LocalDate.of(1995, 5, 5));
        filmWithGenre.setDuration(100);
        filmWithGenre.setRating(mpa);
        filmWithGenre.setGenres(Set.of(genre1));

        Film created = filmRepository.create(filmWithGenre);
        filmRepository.putLike(created.getId(), 1);

        Genre genre2 = new Genre();
        genre2.setId(2);

        Film other = new Film();
        other.setName("other-film");
        other.setDescription("other-film");
        other.setReleaseDate(LocalDate.of(1995, 5, 5));
        other.setDuration(100);
        other.setRating(mpa);
        other.setGenres(Set.of(genre2));

        Film otherCreated = filmRepository.create(other);
        filmRepository.putLike(otherCreated.getId(), 1);
        filmRepository.putLike(otherCreated.getId(), 2);
        filmRepository.putLike(otherCreated.getId(), 3);

        List<Film> popular = filmRepository.getPopular(10, genre1.getId(), null);

        assertThat(popular.stream().map(Film::getId)).contains(created.getId());
        assertThat(popular.stream().map(Film::getId)).doesNotContainNull();
    }

    @Test
    void shouldGetPopularWithGenreAndYear() {
        MpaRating mpa = new MpaRating();
        mpa.setId(1);

        Genre genre3 = new Genre();
        genre3.setId(3);

        Film filmGY = new Film();
        filmGY.setName("genre-year-film");
        filmGY.setDescription("genre-year-film");
        filmGY.setReleaseDate(LocalDate.of(1988, 7, 7));
        filmGY.setDuration(90);
        filmGY.setRating(mpa);
        filmGY.setGenres(Set.of(genre3));

        Film created = filmRepository.create(filmGY);
        filmRepository.putLike(created.getId(), 1);
        filmRepository.putLike(created.getId(), 2);

        List<Film> popular = filmRepository.getPopular(10, genre3.getId(), 1988);

        assertThat(popular.stream().map(Film::getId)).contains(created.getId());
    }

    @Test
    void shouldGetPopularWithYear() {
        MpaRating mpa = new MpaRating();
        mpa.setId(1);

        Genre genre = new Genre();
        genre.setId(1);

        Film filmY = new Film();
        filmY.setName("year-film");
        filmY.setDescription("year-film");
        filmY.setReleaseDate(LocalDate.of(1970, 1, 1));
        filmY.setDuration(110);
        filmY.setRating(mpa);
        filmY.setGenres(Set.of(genre));

        Film created = filmRepository.create(filmY);
        filmRepository.putLike(created.getId(), 1);

        List<Film> popular = filmRepository.getPopular(10, null, 1970);

        assertThat(popular.stream().map(Film::getId)).contains(created.getId());
    }
}

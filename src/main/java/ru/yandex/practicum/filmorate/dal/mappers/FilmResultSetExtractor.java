package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

@Component
public class FilmResultSetExtractor implements ResultSetExtractor<List<Film>> {

    @Override
    public List<Film> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        final Map<Integer, Film> filmMap = new LinkedHashMap<>();

        ResultSetMetaData md = resultSet.getMetaData();
        Set<String> columns = new HashSet<>();
        for (int i = 1; i <= md.getColumnCount(); i++) {
            columns.add(md.getColumnLabel(i).toLowerCase());
        }

        boolean hasDirectorId = columns.contains("director_id");
        boolean hasDirectorName = columns.contains("director_name");
        boolean hasGenreId = columns.contains("genre_id");
        boolean hasGenreName = columns.contains("genre_name");
        boolean hasRatingId = columns.contains("rating_id");
        boolean hasRatingName = columns.contains("rating_name");
        boolean hasReleaseDate = columns.contains("film_release_date");

        while (resultSet.next()) {
            Integer filmId = resultSet.getInt("film_id");

            Film film = filmMap.get(filmId);
            if (film == null) {
                film = new Film();
                film.setId(filmId);
                film.setName(getStringSafe(resultSet, "film_name"));
                film.setDescription(getStringSafe(resultSet, "film_description"));

                if (hasReleaseDate) {
                    java.sql.Date sqlDate = resultSet.getDate("film_release_date");
                    if (sqlDate != null) {
                        film.setReleaseDate(sqlDate.toLocalDate());
                    }
                }

                // duration
                try {
                    int duration = resultSet.getInt("film_duration");
                    if (!resultSet.wasNull()) {
                        film.setDuration(duration);
                    }
                } catch (SQLException ignored) {
                }

                // Устанавливаем рейтинг, если есть
                if (hasRatingId) {
                    int ratingId = resultSet.getInt("rating_id");
                    if (!resultSet.wasNull()) {
                        MpaRating mpaRating = new MpaRating();
                        mpaRating.setId(ratingId);
                        if (hasRatingName) {
                            mpaRating.setName(getStringSafe(resultSet, "rating_name"));
                        }
                        film.setRating(mpaRating);
                    }
                }

                filmMap.put(filmId, film);
            }

            // Добавляем жанр, если он есть
            if (hasGenreId) {
                int genreId = resultSet.getInt("genre_id");
                if (!resultSet.wasNull()) {
                    Genre genre = new Genre();
                    genre.setId(genreId);
                    if (hasGenreName) {
                        genre.setName(getStringSafe(resultSet, "genre_name"));
                    }
                    // избегаем дубликатов
                    if (film.getGenres().stream().noneMatch(g -> g.getId() == genre.getId())) {
                        film.getGenres().add(genre);
                    }
                }
            }

            // Добавляем режиссёра, если соответствующие колонки присутствуют
            if (hasDirectorId) {
                int directorId = resultSet.getInt("director_id");
                if (!resultSet.wasNull()) {
                    Director director = new Director();
                    director.setId(directorId);
                    if (hasDirectorName) {
                        director.setName(getStringSafe(resultSet, "director_name"));
                    }
                    if (film.getDirectors().stream().noneMatch(d -> d.getId() == director.getId())) {
                        film.getDirectors().add(director);
                    }
                }
            }
        }

        return new ArrayList<>(filmMap.values());
    }

    private String getStringSafe(ResultSet rs, String columnLabel) {
        try {
            String s = rs.getString(columnLabel);
            return s;
        } catch (SQLException e) {
            return null;
        }
    }
}

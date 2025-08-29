package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class FilmResultSetExtractor implements ResultSetExtractor<List<Film>> {

    @Override
    public List<Film> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        final Map<Integer, Film> filmMap = new LinkedHashMap<>();

        while (resultSet.next()) {
            Integer filmId = resultSet.getInt("film_id");

            Film film = filmMap.get(filmId);
            if (film == null) {
                film = new Film();
                film.setId(resultSet.getInt("film_id"));
                film.setName(resultSet.getString("film_name"));
                film.setDescription(resultSet.getString("film_description"));
                film.setReleaseDate(resultSet.getDate("film_release_date").toLocalDate());
                film.setDuration(resultSet.getInt("film_duration"));

                // Устанавливаем рейтинг
                MpaRating mpaRating = new MpaRating();
                mpaRating.setId(resultSet.getInt("rating_id"));
                mpaRating.setName(resultSet.getString("rating_name"));
                film.setRating(mpaRating);

                film.setGenres(new HashSet<>());
                filmMap.put(filmId, film);
            }

            // Добавляем жанр, если он есть
            Integer genreId = resultSet.getInt("genre_id");
            if (!resultSet.wasNull()) {
                Genre genre = new Genre();
                genre.setId(genreId);
                genre.setName(resultSet.getString("genre_name"));
                film.getGenres().add(genre);
            }
        }

        return new ArrayList<>(filmMap.values());
    }
}

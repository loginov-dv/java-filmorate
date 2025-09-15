package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

// RowMapper для класса Review
@Component
public class ReviewRowMapper implements RowMapper<Review> {

    // Собирает объект Review из текущей строки ResultSet
    @Override
    public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
        Review review = new Review();
        review.setReviewId(rs.getInt("review_id"));
        review.setContent(rs.getString("content"));
        review.setIsPositive(rs.getBoolean("is_positive"));
        review.setUserId(rs.getInt("user_id"));
        review.setFilmId(rs.getInt("film_id"));
        review.setUseful(rs.getInt("useful"));
        return review;
    }
}
package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

// Репозиторий для работы с отзывами
@Slf4j
@Repository
@RequiredArgsConstructor
public class ReviewRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ReviewRowMapper rowMapper;

    // Создание нового отзыва. Поле useful при создании равно 0.
    public Review create(Review review) {
        log.debug("Создание отзыва: filmId={}, userId={}", review.getFilmId(), review.getUserId());
        String sql = "INSERT INTO reviews (content, is_positive, user_id, film_id, useful) VALUES (?, ?, ?, ?, 0)";
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setInt(3, review.getUserId());
            ps.setInt(4, review.getFilmId());
            return ps;
        }, kh);
        Integer id = kh.getKey() != null ? kh.getKey().intValue() : null;
        review.setReviewId(id);
        review.setUseful(0);
        return review;
    }

    // Получение отзыва по идентификатору
    public Optional<Review> findById(int id) {
        String sql = "SELECT review_id, content, is_positive, user_id, film_id, useful FROM reviews WHERE review_id = ?";
        List<Review> list = jdbcTemplate.query(sql, rowMapper, id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    // Получение списка отзывов. Если filmId указан — только по фильму; иначе все. Сортировка по полезности по убыванию, ограничение count.
    public List<Review> findAllByFilm(Integer filmId, int count) {
        String base = "SELECT review_id, content, is_positive, user_id, film_id, useful FROM reviews";
        if (filmId != null) {
            base += " WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
            return jdbcTemplate.query(base, rowMapper, filmId, count);
        } else {
            base += " ORDER BY useful DESC LIMIT ?";
            return jdbcTemplate.query(base, rowMapper, count);
        }
    }

    // Обновление содержания и знака отзыва
    public Review update(Review review) {
        log.debug("Обновление отзыва id={}", review.getReviewId());
        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
        jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(), review.getReviewId());
        return findById(review.getReviewId()).orElseThrow();
    }

    // Удаление отзыва по идентификатору
    public void delete(int id) {
        log.debug("Удаление отзыва id={}", id);
        jdbcTemplate.update("DELETE FROM reviews WHERE review_id = ?", id);
    }

    // Постановка лайка полезности отзыву
    public void like(int reviewId, int userId) {
        applyReaction(reviewId, userId, true);
    }

    // Постановка дизлайка полезности отзыву
    public void dislike(int reviewId, int userId) {
        applyReaction(reviewId, userId, false);
    }

    // Удаление лайка
    public void removeLike(int reviewId, int userId) {
        removeReaction(reviewId, userId, true);
    }

    // Удаление дизлайка
    public void removeDislike(int reviewId, int userId) {
        removeReaction(reviewId, userId, false);
    }

    // Внутренний метод: применяет реакцию пользователя. Создаёт или меняет запись в review_likes и корректирует useful.
    private void applyReaction(int reviewId, int userId, boolean positive) {
        String q = "SELECT CASE WHEN is_positive THEN 1 ELSE 0 END AS val FROM review_likes WHERE review_id = ? AND user_id = ?";
        List<Integer> res = jdbcTemplate.query(q, (rs, rn) -> rs.getInt("val"), reviewId, userId);
        Integer existing = res.isEmpty() ? null : res.get(0);

        int delta;
        if (existing == null) {
            // реакции ещё не было — вставляем новую
            jdbcTemplate.update("INSERT INTO review_likes (review_id, user_id, is_positive) VALUES (?, ?, ?)",
                    reviewId, userId, positive);
            delta = positive ? 1 : -1;
        } else if ((existing == 1) == positive) {
            // уже стоит такая же реакция — ничего не меняем
            return;
        } else {
            // меняем реакцию на противоположную
            jdbcTemplate.update("UPDATE review_likes SET is_positive = ? WHERE review_id = ? AND user_id = ?",
                    positive, reviewId, userId);
            delta = positive ? 2 : -2; // -1 -> +1 (или наоборот): суммарное изменение на 2
        }
        jdbcTemplate.update("UPDATE reviews SET useful = useful + ? WHERE review_id = ?", delta, reviewId);
    }

    // Внутренний метод: удаляет реакцию и откатывает useful.
    private void removeReaction(int reviewId, int userId, boolean positive) {
        String q = "SELECT CASE WHEN is_positive THEN 1 ELSE 0 END AS val FROM review_likes WHERE review_id = ? AND user_id = ?";
        List<Integer> res = jdbcTemplate.query(q, (rs, rn) -> rs.getInt("val"), reviewId, userId);
        Integer existing = res.isEmpty() ? null : res.get(0);

        if (existing == null) {
            return; // нечего удалять
        }
        if ((existing == 1) == positive) {
            jdbcTemplate.update("DELETE FROM review_likes WHERE review_id = ? AND user_id = ?", reviewId, userId);
            int delta = positive ? -1 : 1;
            jdbcTemplate.update("UPDATE reviews SET useful = useful + ? WHERE review_id = ?", delta, reviewId);
        }
    }
}

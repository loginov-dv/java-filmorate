package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

// Репозиторий для работы с отзывами
@Slf4j
@Repository
public class ReviewRepository extends BaseRepository<Review> {

    // Явный конструктор: передаём зависимости в базовый класс
    public ReviewRepository(JdbcTemplate jdbcTemplate, ReviewRowMapper rowMapper) {
        super(jdbcTemplate, rowMapper);
    }

    // Создание нового отзыва. Поле useful при создании равно 0
    public Review create(Review review) {
        log.debug("Создание отзыва: filmId={}, userId={}", review.getFilmId(), review.getUserId());
        String sql = "INSERT INTO reviews (content, is_positive, user_id, film_id, useful) VALUES (?, ?, ?, ?, 0)";
        int id = insert(sql, review.getContent(), review.getIsPositive(), review.getUserId(), review.getFilmId());
        review.setReviewId(id);
        review.setUseful(0);
        return review;
    }

    // Получение отзыва по идентификатору
    public Optional<Review> findById(int id) {
        String sql = "SELECT review_id, content, is_positive, user_id, film_id, useful FROM reviews WHERE review_id = ?";
        return findOne(sql, id);
    }

    // Получение списка отзывов
    public List<Review> findAllByFilm(Integer filmId, int count) {
        String base = "SELECT review_id, content, is_positive, user_id, film_id, useful FROM reviews";
        if (filmId != null) {
            base += " WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
            return findMany(base, filmId, count);
        } else {
            base += " ORDER BY useful DESC LIMIT ?";
            return findMany(base, count);
        }
    }

    // Обновление содержания и знака отзыва
    public Review update(Review review) {
        log.debug("Обновление отзыва id={}", review.getReviewId());
        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
        update(sql, review.getContent(), review.getIsPositive(), review.getReviewId());
        return findById(review.getReviewId()).orElseThrow();
    }

    // Удаление отзыва по идентификатору
    public void delete(int id) {
        log.debug("Удаление отзыва id={}", id);
        update("DELETE FROM reviews WHERE review_id = ?", id);
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

    // Внутренний метод: применяет реакцию пользователя
    private void applyReaction(int reviewId, int userId, boolean positive) {
        // 1 — лайк, 0 — дизлайк
        String q = "SELECT CASE WHEN is_positive THEN 1 ELSE 0 END AS val " +
                "FROM review_likes WHERE review_id = ? AND user_id = ?";
        List<Integer> res = findManyInts(q, reviewId, userId);
        Integer existing = res.isEmpty() ? null : res.get(0);

        int delta;
        if (existing == null) {
            // реакции ещё не было — вставляем новую
            update("INSERT INTO review_likes (review_id, user_id, is_positive) VALUES (?, ?, ?)",
                    reviewId, userId, positive);
            delta = positive ? 1 : -1;
        } else if ((existing == 1) == positive) {
            // уже стоит такая же реакция — ничего не меняется
            return;
        } else {
            // меняем реакцию на противоположную
            update("UPDATE review_likes SET is_positive = ? WHERE review_id = ? AND user_id = ?",
                    positive, reviewId, userId);
            // переключение с -1 на +1 (или обратно) меняет суммарно на 2
            delta = positive ? 2 : -2;
        }

        // Корректируем рейтинг полезности
        update("UPDATE reviews SET useful = useful + ? WHERE review_id = ?", delta, reviewId);
    }

    // Внутренний метод: удаляет реакцию и откатывает useful
    private void removeReaction(int reviewId, int userId, boolean positive) {
        String q = "SELECT CASE WHEN is_positive THEN 1 ELSE 0 END AS val " +
                "FROM review_likes WHERE review_id = ? AND user_id = ?";
        List<Integer> res = findManyInts(q, reviewId, userId);
        Integer existing = res.isEmpty() ? null : res.get(0);

        if (existing == null) {
            return; // нечего удалять
        }
        if ((existing == 1) == positive) {
            update("DELETE FROM review_likes WHERE review_id = ? AND user_id = ?", reviewId, userId);
            int delta = positive ? -1 : 1;
            update("UPDATE reviews SET useful = useful + ? WHERE review_id = ?", delta, reviewId);
        }
    }
}

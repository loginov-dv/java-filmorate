package ru.yandex.practicum.filmorate.dal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dal.mappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * JDBC-тесты ReviewRepository на H2, без веб-слоя.
 */
@JdbcTest
@Import({ReviewRepository.class, ReviewRowMapper.class})
@Sql(statements = {
        // Минимальная подготовка
        "DELETE FROM review_likes",
        "DELETE FROM reviews",
        "DELETE FROM film_genres",
        "DELETE FROM films",
        "DELETE FROM users",
        "INSERT INTO users(user_id, email, login, name, birthday) VALUES (1,'u1@mail','u1','U1','1990-01-01')",
        "INSERT INTO users(user_id, email, login, name, birthday) VALUES (2,'u2@mail','u2','U2','1990-01-01')",
        "INSERT INTO films(film_id, name, description, release_date, duration, rating_id) " +
                "VALUES (1,'F','D','2000-01-01',100,1)"
})
class ReviewRepositoryTest {

    @Autowired
    ReviewRepository reviewRepository;

    @Test
    @DisplayName("create + findById — создаёт и читает отзыв с useful=0")
    void createAndFind() {
        Review r = new Review();
        r.setContent("Text");
        r.setIsPositive(true);
        r.setUserId(1);
        r.setFilmId(1);

        Review created = reviewRepository.create(r);

        assertThat(created.getReviewId()).isNotNull();
        assertThat(created.getUseful()).isEqualTo(0);

        Optional<Review> fromDb = reviewRepository.findById(created.getReviewId());
        assertThat(fromDb).isPresent();
        assertThat(fromDb.get().getContent()).isEqualTo("Text");
    }

    @Test
    @DisplayName("like/dislike меняют useful: 0→+1, +1→-1 (дельта -2), удаление возвращает 0")
    void reactionsDeltas() {
        Review r = new Review();
        r.setContent("X");
        r.setIsPositive(true);
        r.setUserId(1);
        r.setFilmId(1);
        Review created = reviewRepository.create(r);
        int id = created.getReviewId();

        reviewRepository.like(id, 1);
        assertThat(reviewRepository.findById(id)).get().extracting(Review::getUseful).isEqualTo(1);

        reviewRepository.dislike(id, 1);
        assertThat(reviewRepository.findById(id)).get().extracting(Review::getUseful).isEqualTo(-1);

        reviewRepository.removeDislike(id, 1);
        assertThat(reviewRepository.findById(id)).get().extracting(Review::getUseful).isEqualTo(0);
    }

    @Test
    @DisplayName("update — изменяет редактируемые поля (content, isPositive)")
    void update() {
        Review r = new Review();
        r.setContent("A");
        r.setIsPositive(true);
        r.setUserId(1);
        r.setFilmId(1);
        Review created = reviewRepository.create(r);

        created.setContent("B");
        created.setIsPositive(false);
        Review updated = reviewRepository.update(created);

        assertThat(updated.getContent()).isEqualTo("B");
        assertThat(updated.getIsPositive()).isFalse();
    }
}

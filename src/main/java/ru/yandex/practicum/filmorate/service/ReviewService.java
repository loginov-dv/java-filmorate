package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.ReviewRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Service
public class ReviewService {
    // Логгер
    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         FilmRepository filmRepository,
                         UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.filmRepository = filmRepository;
        this.userRepository = userRepository;
    }

    // Создание нового отзыва
    public Review create(Review review) {
        logger.debug("Запрос на создание отзыва: filmId={}, userId={}", review.getFilmId(), review.getUserId());
        validateForCreate(review);
        userRepository.getById(review.getUserId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + review.getUserId() + " не найден"));
        filmRepository.getById(review.getFilmId())
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + review.getFilmId() + " не найден"));

        return reviewRepository.create(review);
    }

    // Обновление текста/тональности отзыва
    public Review update(Review review) {
        logger.debug("Запрос на обновление отзыва id={}", review.getReviewId());
        if (review.getReviewId() == null) {
            throw new NotFoundException("Отзыв с id = null не найден");
        }
        // убеждаемся, что отзыв существует
        Review existing = reviewRepository.findById(review.getReviewId())
                .orElseThrow(() -> new NotFoundException("Отзыв с id = " + review.getReviewId() + " не найден"));

        // обновляем только редактируемые поля
        if (review.getContent() != null) {
            existing.setContent(review.getContent());
        }
        if (review.getIsPositive() != null) {
            existing.setIsPositive(review.getIsPositive());
        }
        return reviewRepository.update(existing);
    }

    // Удаление отзыва
    public void delete(int id) {
        logger.debug("Запрос на удаление отзыва id={}", id);
        reviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв с id = " + id + " не найден"));
        reviewRepository.delete(id);
    }

    // Получение одного отзыва по id
    public Review getById(int id) {
        logger.debug("Запрос на получение отзыва id={}", id);
        return reviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв с id = " + id + " не найден"));
    }

    // Получение списка отзывов по фильму (или всех), отсортировано по useful, ограничено count
    public List<Review> getAllByFilm(Integer filmId, int count) {
        logger.debug("Запрос на получение отзывов: filmId={}, count={}", filmId, count);
        if (filmId != null) {
            // если фильм указан — проверим, что он существует
            filmRepository.getById(filmId)
                    .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден"));
        }
        return reviewRepository.findAllByFilm(filmId, count);
    }

    // Пользователь ставит лайк отзыву
    public void putLike(int reviewId, int userId) {
        logger.debug("Запрос на установку лайка: reviewId={}, userId={}", reviewId, userId);
        ensureReviewAndUserExist(reviewId, userId);
        reviewRepository.like(reviewId, userId);
    }

    // Пользователь ставит дизлайк отзыву
    public void putDislike(int reviewId, int userId) {
        logger.debug("Запрос на установку дизлайка: reviewId={}, userId={}", reviewId, userId);
        ensureReviewAndUserExist(reviewId, userId);
        reviewRepository.dislike(reviewId, userId);
    }

    // Пользователь удаляет лайк отзыву
    public void removeLike(int reviewId, int userId) {
        logger.debug("Запрос на удаление лайка: reviewId={}, userId={}", reviewId, userId);
        ensureReviewAndUserExist(reviewId, userId);
        reviewRepository.removeLike(reviewId, userId);
    }

    // Пользователь удаляет дизлайк отзыву
    public void removeDislike(int reviewId, int userId) {
        logger.debug("Запрос на удаление дизлайка: reviewId={}, userId={}", reviewId, userId);
        ensureReviewAndUserExist(reviewId, userId);
        reviewRepository.removeDislike(reviewId, userId);
    }

    // Базовая валидация данных при создании
    private void validateForCreate(Review r) {
        if (r.getContent() == null || r.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Текст отзыва не может быть пустым");
        }
        if (r.getIsPositive() == null) {
            throw new IllegalArgumentException("Поле isPositive должно быть указано");
        }
        if (r.getUserId() == null) {
            throw new IllegalArgumentException("userId должен быть указан");
        }
        if (r.getFilmId() == null) {
            throw new IllegalArgumentException("filmId должен быть указан");
        }
    }

    // Проверка существования отзыва и пользователя
    private void ensureReviewAndUserExist(int reviewId, int userId) {
        reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыв с id = " + reviewId + " не найден"));
        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }
}
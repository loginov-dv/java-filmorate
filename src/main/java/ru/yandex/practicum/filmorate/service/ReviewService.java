package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.ReviewRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dto.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
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
    @Transactional
    public Review create(Review review) {
        logger.debug("Запрос на создание отзыва: filmId={}, userId={}", review.getFilmId(), review.getUserId());

        // Бизнес-проверки FK
        userRepository.getById(review.getUserId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + review.getUserId() + " не найден"));
        filmRepository.getById(review.getFilmId())
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + review.getFilmId() + " не найден"));

        // В репозитории useful инициализируется 0, либо уже в БД по умолчанию
        return reviewRepository.create(review);
    }

    // Обновление отзыва: используем ReviewMapper.updateReviewFields(...)
    @Transactional
    public Review update(UpdateReviewRequest request) {
        logger.debug("Запрос на обновление отзыва id={}", request.getReviewId());
        if (request.getReviewId() == null) {
            throw new NotFoundException("Отзыв с id = null не найден");
        }

        // Убеждаемся, что отзыв существует
        Review existing = reviewRepository.findById(request.getReviewId())
                .orElseThrow(() -> new NotFoundException("Отзыв с id = " + request.getReviewId() + " не найден"));

        // Применяем изменения централизованно через маппер
        ReviewMapper.updateReviewFields(existing, request);

        return reviewRepository.update(existing);
    }

    // Удаление отзыва
    @Transactional
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

    // Получение списка отзывов по фильму, сортировка по useful DESC, ограничение count
    public List<Review> getAllByFilm(Integer filmId, int count) {
        logger.debug("Запрос на получение отзывов: filmId={}, count={}", filmId, count);
        if (filmId != null) {
            // Если фильм указан — проверка, что он существует
            filmRepository.getById(filmId)
                    .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден"));
        }
        return reviewRepository.findAllByFilm(filmId, count);
    }

    // Пользователь ставит лайк отзыву
    @Transactional
    public void putLike(int reviewId, int userId) {
        logger.debug("Запрос на установку лайка: reviewId={}, userId={}", reviewId, userId);
        ensureReviewAndUserExist(reviewId, userId);
        reviewRepository.like(reviewId, userId);
    }

    // Пользователь ставит дизлайк отзыву
    @Transactional
    public void putDislike(int reviewId, int userId) {
        logger.debug("Запрос на установку дизлайка: reviewId={}, userId={}", reviewId, userId);
        ensureReviewAndUserExist(reviewId, userId);
        reviewRepository.dislike(reviewId, userId);
    }

    // Пользователь удаляет лайк отзыву
    @Transactional
    public void removeLike(int reviewId, int userId) {
        logger.debug("Запрос на удаление лайка: reviewId={}, userId={}", reviewId, userId);
        ensureReviewAndUserExist(reviewId, userId);
        reviewRepository.removeLike(reviewId, userId);
    }

    // Пользователь удаляет дизлайк отзыву
    @Transactional
    public void removeDislike(int reviewId, int userId) {
        logger.debug("Запрос на удаление дизлайка: reviewId={}, userId={}", reviewId, userId);
        ensureReviewAndUserExist(reviewId, userId);
        reviewRepository.removeDislike(reviewId, userId);
    }

    // Проверка существования отзыва и пользователя
    private void ensureReviewAndUserExist(int reviewId, int userId) {
        reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыв с id = " + reviewId + " не найден"));
        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }
}

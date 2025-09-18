package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.EventRepository;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.ReviewRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dto.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.events.Event;
import ru.yandex.practicum.filmorate.model.events.EventType;
import ru.yandex.practicum.filmorate.model.events.Operation;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    // final-поля будут инжектиться через сгенерированный конструктор
    private final ReviewRepository reviewRepository;
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    // Создание нового отзыва
    @Transactional
    public Review create(Review review) {
        log.debug("Запрос на создание отзыва: filmId={}, userId={}", review.getFilmId(), review.getUserId());

        // Бизнес-проверки FK
        userRepository.getById(review.getUserId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + review.getUserId() + " не найден"));
        filmRepository.getById(review.getFilmId())
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + review.getFilmId() + " не найден"));

        Review newReview = reviewRepository.create(review);

        eventRepository.create(new Event(newReview.getUserId(), newReview.getReviewId(),
                EventType.REVIEW, Operation.ADD));

        return newReview;
    }

    // Обновление отзыва
    @Transactional
    public Review update(UpdateReviewRequest request) {
        log.debug("Запрос на обновление отзыва id={}", request.getReviewId());
        if (request.getReviewId() == null) {
            throw new NotFoundException("Отзыв с id = null не найден");
        }
        Review existing = reviewRepository.findById(request.getReviewId())
                .orElseThrow(() -> new NotFoundException("Отзыв с id = " + request.getReviewId() + " не найден"));

        ReviewMapper.updateReviewFields(existing, request);

        eventRepository.create(new Event(existing.getUserId(), existing.getReviewId(),
                EventType.REVIEW, Operation.UPDATE));

        return reviewRepository.update(existing);
    }

    // Удаление отзыва
    @Transactional
    public void delete(int id) {
        log.debug("Запрос на удаление отзыва id={}", id);

        Optional<Review> maybeReview = reviewRepository.findById(id);

        if (maybeReview.isEmpty()) {
            throw new NotFoundException("Отзыв с id = " + id + " не найден");
        }

        Review review = maybeReview.get();

        eventRepository.create(new Event(review.getUserId(), review.getReviewId(),
                EventType.REVIEW, Operation.REMOVE));

        reviewRepository.delete(id);
    }

    // Получение одного отзыва
    public Review getById(int id) {
        log.debug("Запрос на получение отзыва id={}", id);
        return reviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв с id = " + id + " не найден"));
    }

    // Получение списка отзывов
    public List<Review> getAllByFilm(Integer filmId, int count) {
        log.debug("Запрос на получение отзывов: filmId={}, count={}", filmId, count);
        if (filmId != null) {
            filmRepository.getById(filmId)
                    .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден"));
        }
        return reviewRepository.findAllByFilm(filmId, count);
    }

    // Реакции
    @Transactional
    public void putLike(int reviewId, int userId) {
        log.debug("Запрос на установку лайка: reviewId={}, userId={}", reviewId, userId);
        ensureReviewAndUserExist(reviewId, userId);
        reviewRepository.like(reviewId, userId);
    }

    @Transactional
    public void putDislike(int reviewId, int userId) {
        log.debug("Запрос на установку дизлайка: reviewId={}, userId={}", reviewId, userId);
        ensureReviewAndUserExist(reviewId, userId);
        reviewRepository.dislike(reviewId, userId);
    }

    @Transactional
    public void removeLike(int reviewId, int userId) {
        log.debug("Запрос на удаление лайка: reviewId={}, userId={}", reviewId, userId);
        ensureReviewAndUserExist(reviewId, userId);
        reviewRepository.removeLike(reviewId, userId);
    }

    @Transactional
    public void removeDislike(int reviewId, int userId) {
        log.debug("Запрос на удаление дизлайка: reviewId={}, userId={}", reviewId, userId);
        ensureReviewAndUserExist(reviewId, userId);
        reviewRepository.removeDislike(reviewId, userId);
    }

    // Проверка существования сущностей
    private void ensureReviewAndUserExist(int reviewId, int userId) {
        reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыв с id = " + reviewId + " не найден"));
        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }
}

package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

// Контроллер для работы с отзывами
@RestController
@RequestMapping("/reviews")
public class ReviewController {
    // Логгер
    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // Эндпоинт POST /reviews — добавление нового отзыва
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review create(@Valid @RequestBody Review review) {
        logger.debug("Вызов эндпоинта POST /reviews");
        return reviewService.create(review);
    }

    // Эндпоинт PUT /reviews — редактирование отзыва
    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        logger.debug("Вызов эндпоинта PUT /reviews");
        return reviewService.update(review);
    }

    // Эндпоинт DELETE /reviews/{id} — удаление отзыва
    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        logger.debug("Вызов эндпоинта DELETE /reviews/{}", id);
        reviewService.delete(id);
    }

    // Эндпоинт GET /reviews/{id} — получение отзыва по id
    @GetMapping("/{id}")
    public Review getById(@PathVariable int id) {
        logger.debug("Вызов эндпоинта GET /reviews/{}", id);
        return reviewService.getById(id);
    }

    // Эндпоинт GET /reviews?filmId={filmId}&count={count}
    @GetMapping
    public List<Review> getAll(@RequestParam(value = "filmId", required = false) Integer filmId,
                               @RequestParam(value = "count", defaultValue = "10") int count) {
        logger.debug("Вызов эндпоинта GET /reviews c параметрами filmId={}, count={}", filmId, count);
        return reviewService.getAllByFilm(filmId, count);
    }

    // Эндпоинт PUT /reviews/{id}/like/{userId} — поставить лайк отзыву
    @PutMapping("/{id}/like/{userId}")
    public void putLike(@PathVariable int id, @PathVariable int userId) {
        logger.debug("Вызов эндпоинта PUT /reviews/{}/like/{}", id, userId);
        reviewService.putLike(id, userId);
    }

    // Эндпоинт PUT /reviews/{id}/dislike/{userId} — поставить дизлайк отзыву
    @PutMapping("/{id}/dislike/{userId}")
    public void putDislike(@PathVariable int id, @PathVariable int userId) {
        logger.debug("Вызов эндпоинта PUT /reviews/{}/dislike/{}", id, userId);
        reviewService.putDislike(id, userId);
    }

    // Эндпоинт DELETE /reviews/{id}/like/{userId} — удалить лайк
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        logger.debug("Вызов эндпоинта DELETE /reviews/{}/like/{}", id, userId);
        reviewService.removeLike(id, userId);
    }

    // Эндпоинт DELETE /reviews/{id}/dislike/{userId} — удалить дизлайк
    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable int id, @PathVariable int userId) {
        logger.debug("Вызов эндпоинта DELETE /reviews/{}/dislike/{}", id, userId);
        reviewService.removeDislike(id, userId);
    }
}
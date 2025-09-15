package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;
import java.util.stream.Collectors;

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
    public ReviewDto create(@Valid @RequestBody NewReviewRequest request) {
        logger.debug("Вызов эндпоинта POST /reviews");
        Review created = reviewService.create(ReviewMapper.mapToReview(request));
        return ReviewMapper.mapToReviewDto(created);
    }

    // Эндпоинт PUT /reviews — редактирование отзыва
    @PutMapping
    public ReviewDto update(@Valid @RequestBody UpdateReviewRequest request) {
        logger.debug("Вызов эндпоинта PUT /reviews");
        // Сервис сам подтянет существующий отзыв и применит изменения
        Review patch = ReviewMapper.mapToReview(request);
        Review updated = reviewService.update(patch);
        return ReviewMapper.mapToReviewDto(updated);
    }

    // Эндпоинт DELETE /reviews/{id} — удаление отзыва
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        logger.debug("Вызов эндпоинта DELETE /reviews/{}", id);
        reviewService.delete(id);
    }

    // Эндпоинт GET /reviews/{id} — получение отзыва по id
    @GetMapping("/{id}")
    public ReviewDto getById(@PathVariable int id) {
        logger.debug("Вызов эндпоинта GET /reviews/{}", id);
        return ReviewMapper.mapToReviewDto(reviewService.getById(id));
    }

    // Эндпоинт GET /reviews?filmId={filmId}&count={count}
    @GetMapping
    public List<ReviewDto> getAll(@RequestParam(value = "filmId", required = false) Integer filmId,
                                  @RequestParam(value = "count", defaultValue = "10") int count) {
        logger.debug("Вызов эндпоинта GET /reviews c параметрами filmId={}, count={}", filmId, count);
        return reviewService.getAllByFilm(filmId, count).stream()
                .map(ReviewMapper::mapToReviewDto)
                .collect(Collectors.toList());
    }

    // Эндпоинт PUT /reviews/{id}/like/{userId} — поставить лайк отзыву
    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void putLike(@PathVariable int id, @PathVariable int userId) {
        logger.debug("Вызов эндпоинта PUT /reviews/{}/like/{}", id, userId);
        reviewService.putLike(id, userId);
    }

    // Эндпоинт PUT /reviews/{id}/dislike/{userId} — поставить дизлайк отзыву
    @PutMapping("/{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void putDislike(@PathVariable int id, @PathVariable int userId) {
        logger.debug("Вызов эндпоинта PUT /reviews/{}/dislike/{}", id, userId);
        reviewService.putDislike(id, userId);
    }

    // Эндпоинт DELETE /reviews/{id}/like/{userId} — удалить лайк
    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        logger.debug("Вызов эндпоинта DELETE /reviews/{}/like/{}", id, userId);
        reviewService.removeLike(id, userId);
    }

    // Эндпоинт DELETE /reviews/{id}/dislike/{userId} — удалить дизлайк
    @DeleteMapping("/{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeDislike(@PathVariable int id, @PathVariable int userId) {
        logger.debug("Вызов эндпоинта DELETE /reviews/{}/dislike/{}", id, userId);
        reviewService.removeDislike(id, userId);
    }
}
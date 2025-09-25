package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor // генерирует конструктор для final-полей
public class ReviewController {
    // Сервис инжектится через сгенерированный Lombok-конструктор
    private final ReviewService reviewService;

    // Эндпоинт POST /reviews — добавление нового отзыва (200 OK)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDto create(@Valid @RequestBody NewReviewRequest request) {
        log.debug("Вызов эндпоинта POST /reviews");
        Review created = reviewService.create(ReviewMapper.mapToReview(request));
        return ReviewMapper.mapToReviewDto(created);
    }

    // Эндпоинт PUT /reviews — редактирование отзыва
    @PutMapping
    public ReviewDto update(@Valid @RequestBody UpdateReviewRequest request) {
        log.debug("Вызов эндпоинта PUT /reviews");
        Review updated = reviewService.update(request); // используем маппер внутри сервиса
        return ReviewMapper.mapToReviewDto(updated);
    }

    // Эндпоинт DELETE /reviews/{id} — удаление отзыва
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive int id) {
        log.debug("Вызов эндпоинта DELETE /reviews/{}", id);
        reviewService.delete(id);
    }

    // Эндпоинт GET /reviews/{id} — получение отзыва по id
    @GetMapping("/{id}")
    public ReviewDto getById(@PathVariable @Positive int id) {
        log.debug("Вызов эндпоинта GET /reviews/{}", id);
        return ReviewMapper.mapToReviewDto(reviewService.getById(id));
    }

    // Эндпоинт GET /reviews?filmId={filmId}&count={count}
    @GetMapping
    public List<ReviewDto> getAll(@RequestParam(value = "filmId", required = false) @Positive Integer filmId,
                                  @RequestParam(value = "count", defaultValue = "10") @Positive int count) {
        log.debug("Вызов эндпоинта GET /reviews c параметрами filmId={}, count={}", filmId, count);
        return reviewService.getAllByFilm(filmId, count).stream()
                .map(ReviewMapper::mapToReviewDto)
                .collect(Collectors.toList());
    }

    // Эндпоинт PUT /reviews/{id}/like/{userId} — поставить лайк отзыву
    @PutMapping("/{id}/like/{userId}")
    public void putLike(@PathVariable @Positive int id,
                        @PathVariable @Positive int userId) {
        log.debug("Вызов эндпоинта PUT /reviews/{}/like/{}", id, userId);
        reviewService.putLike(id, userId);
    }

    // Эндпоинт PUT /reviews/{id}/dislike/{userId} — поставить дизлайк отзыву
    @PutMapping("/{id}/dislike/{userId}")
    public void putDislike(@PathVariable @Positive int id,
                           @PathVariable @Positive int userId) {
        log.debug("Вызов эндпоинта PUT /reviews/{}/dislike/{}", id, userId);
        reviewService.putDislike(id, userId);
    }

    // Эндпоинт DELETE /reviews/{id}/like/{userId} — удалить лайк
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable @Positive int id,
                           @PathVariable @Positive int userId) {
        log.debug("Вызов эндпоинта DELETE /reviews/{}/like/{}", id, userId);
        reviewService.removeLike(id, userId);
    }

    // Эндпоинт DELETE /reviews/{id}/dislike/{userId} — удалить дизлайк
    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable @Positive int id,
                              @PathVariable @Positive int userId) {
        log.debug("Вызов эндпоинта DELETE /reviews/{}/dislike/{}", id, userId);
        reviewService.removeDislike(id, userId);
    }
}
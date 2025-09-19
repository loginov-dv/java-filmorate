package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor // генерирует конструктор для final-полей
public class ReviewController {

    // Сервис инжектится через сгенерированный Lombok-конструктор
    private final ReviewService reviewService;

    // Эндпоинт POST /reviews — добавление нового отзыва (200 OK)
    @PostMapping
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
    public void delete(@PathVariable int id) {
        log.debug("Вызов эндпоинта DELETE /reviews/{}", id);
        reviewService.delete(id);
    }

    // Эндпоинт GET /reviews/{id} — получение отзыва по id
    @GetMapping("/{id}")
    public ReviewDto getById(@PathVariable int id) {
        log.debug("Вызов эндпоинта GET /reviews/{}", id);
        return ReviewMapper.mapToReviewDto(reviewService.getById(id));
    }

    // Эндпоинт GET /reviews?filmId={filmId}&count={count}
    @GetMapping
    public List<ReviewDto> getAll(@RequestParam(value = "filmId", required = false) Integer filmId,
                                  @RequestParam(value = "count", defaultValue = "10") int count) {
        log.debug("Вызов эндпоинта GET /reviews c параметрами filmId={}, count={}", filmId, count);
        return reviewService.getAllByFilm(filmId, count).stream()
                .map(ReviewMapper::mapToReviewDto)
                .collect(Collectors.toList());
    }

    // Эндпоинт PUT /reviews/{id}/like/{userId} — поставить лайк отзыву
    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void putLike(@PathVariable int id, @PathVariable int userId) {
        log.debug("Вызов эндпоинта PUT /reviews/{}/like/{}", id, userId);
        reviewService.putLike(id, userId);
    }

    // Эндпоинт PUT /reviews/{id}/dislike/{userId} — поставить дизлайк отзыву
    @PutMapping("/{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void putDislike(@PathVariable int id, @PathVariable int userId) {
        log.debug("Вызов эндпоинта PUT /reviews/{}/dislike/{}", id, userId);
        reviewService.putDislike(id, userId);
    }

    // Эндпоинт DELETE /reviews/{id}/like/{userId} — удалить лайк
    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        log.debug("Вызов эндпоинта DELETE /reviews/{}/like/{}", id, userId);
        reviewService.removeLike(id, userId);
    }

    // Эндпоинт DELETE /reviews/{id}/dislike/{userId} — удалить дизлайк
    @DeleteMapping("/{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeDislike(@PathVariable int id, @PathVariable int userId) {
        log.debug("Вызов эндпоинта DELETE /reviews/{}/dislike/{}", id, userId);
        reviewService.removeDislike(id, userId);
    }
}
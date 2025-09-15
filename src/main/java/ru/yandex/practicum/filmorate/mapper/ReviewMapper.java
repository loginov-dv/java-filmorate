package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.model.Review;

// Маппер для преобразования Review и DTO
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReviewMapper {

    // Создаёт доменную модель Review из запроса на создание
    public static Review mapToReview(NewReviewRequest req) {
        Review review = new Review();
        review.setContent(req.getContent());
        review.setIsPositive(req.getIsPositive());
        review.setUserId(req.getUserId());
        review.setFilmId(req.getFilmId());
        review.setUseful(0);
        return review;
    }

    // Создаёт «патч» Review из запроса на обновление
    public static Review mapToReview(UpdateReviewRequest req) {
        Review review = new Review();
        review.setReviewId(req.getReviewId());
        review.setContent(req.getContent());
        review.setIsPositive(req.getIsPositive());
        return review;
    }

    // Обновляет поля существующего Review данными из запроса на изменение
    public static Review updateReviewFields(Review target, UpdateReviewRequest req) {
        if (req.getContent() != null) {
            target.setContent(req.getContent());
        }
        if (req.getIsPositive() != null) {
            target.setIsPositive(req.getIsPositive());
        }
        return target;
    }

    // Преобразует доменную модель Review в DTO для ответа клиенту
    public static ReviewDto mapToReviewDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setReviewId(review.getReviewId());
        dto.setContent(review.getContent());
        dto.setIsPositive(review.getIsPositive());
        dto.setUserId(review.getUserId());
        dto.setFilmId(review.getFilmId());
        dto.setUseful(review.getUseful());
        return dto;
    }
}

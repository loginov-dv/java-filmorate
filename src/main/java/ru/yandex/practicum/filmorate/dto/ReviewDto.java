package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

@Data
public class ReviewDto {
    private Integer reviewId;
    private String content;
    private Boolean isPositive;
    private Integer userId; // Пользователь
    private Integer filmId; // Фильм
    private Integer useful; // Рейтинг полезности
}
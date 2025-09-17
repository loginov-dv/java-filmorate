package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateReviewRequest {
    @NotNull
    private Integer reviewId;

    @NotBlank
    private String content;

    @NotNull
    private Boolean isPositive;
}

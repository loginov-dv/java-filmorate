package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

// Модель данных для описания фильма
@Getter
@Setter
@Builder
@EqualsAndHashCode(exclude = {"id", "description", "likes"})
@ToString
public class Film {
    // Идентификатор
    private Integer id;
    // Название
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    // Описание
    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    @NotBlank(message = "Описание должно быть заполнено")
    private String description;
    // Дата релиза
    @ReleaseDate
    private LocalDate releaseDate;
    // Продолжительность
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private int duration;
    // Лайки пользователей
    @Setter(AccessLevel.NONE)
    @Builder.Default
    private Set<Integer> likes = new HashSet<>();

    public void addLike(int id) {
        if (likes == null) {
            likes = new HashSet<>();
        }

        likes.add(id);
    }

    public void removeLike(int id) {
        if (likes == null) {
            return;
        }

        likes.remove(id);
    }
}

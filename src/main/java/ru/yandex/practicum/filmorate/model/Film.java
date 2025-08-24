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
@EqualsAndHashCode(exclude = {"id", "description"/*, "likes"*/})
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
    // MPA-рейтинг
    private MpaRating rating;
    // Жанры
    private Set<Genre> genres;

    /*
    // Лайки пользователей
    @Setter(AccessLevel.NONE)
    private Set<Integer> likes = new HashSet<>();

    // Добавить лайк пользователя с указанным id
    public void addLike(int id) {
        if (likes == null) {
            likes = new HashSet<>();
        }

        likes.add(id);
    }

    // Убрать лайк пользователя с указанным id
    public void removeLike(int id) {
        if (likes == null) {
            return;
        }

        likes.remove(id);
    }

    // Убрать все лайки пользователей
    public void removeAllLikes() {
        if (likes == null) {
            return;
        }

        likes.clear();
    }
    */
}

package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

// Класс, описывающий дружескую связь между пользователями
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Friendship {
    private Integer userId;
    private Integer friendId;
}
# Диаграмма БД

![ER-diagram](/database-diagram.png)

## Описание таблиц

* `film` - фильмы
* `genre` - жанр
* `rating` - MPA-рейтинг
* `film_genre` - промежуточная таблица для связи фильмов и жанров (у фильма может быть несколько жанров)
* `user` - пользователь
* `film_likes` - промежуточная таблица для лайков пользователей
* `friendship` - промежуточная таблица для дружеских связей

## Примеры запросов

### Получить все фильмы
```
SELECT *
FROM film;
```

### Получить жанры фильма с указанным id

```
SELECT g.name
FROM film AS f
INNER JOIN film_genre AS fg ON f.film_id = fg.film_id
INNER JOIN genre AS g ON fg.genre_id = g.genre_id
WHERE f.film_id = <искомый id>;
```

### Получить количество лайков фильма с указанным id
```
SELECT COUNT(*)
FROM film AS f
INNER JOIN film_likes AS fl ON f.film_id = fl.film_id
WHERE f.film_id = <искомый id>
GROUP BY fl.film_id;
```
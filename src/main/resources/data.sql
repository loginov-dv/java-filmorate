DELETE FROM friendships;
DELETE FROM film_likes;
DELETE FROM film_genres;
DELETE FROM films;
DELETE FROM users;
DELETE FROM genres;
DELETE FROM ratings;

INSERT INTO genres(name) VALUES('Комедия');
INSERT INTO genres(name) VALUES('Драма');
INSERT INTO genres(name) VALUES('Мультфильм');
INSERT INTO genres(name) VALUES('Триллер');
INSERT INTO genres(name) VALUES('Документальный');
INSERT INTO genres(name) VALUES('Боевик');

INSERT INTO ratings(name) VALUES('G');
INSERT INTO ratings(name) VALUES('PG');
INSERT INTO ratings(name) VALUES('PG-13');
INSERT INTO ratings(name) VALUES('R');
INSERT INTO ratings(name) VALUES('NC-17');
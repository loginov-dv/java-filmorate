DELETE FROM events;
DELETE FROM review_likes;
DELETE FROM reviews;
DELETE FROM friendships;
DELETE FROM film_likes;
DELETE FROM film_genres;
DELETE FROM film_directors;
DELETE FROM films;
DELETE FROM users;
DELETE FROM genres;
DELETE FROM ratings;
DELETE FROM directors;

ALTER TABLE events ALTER COLUMN event_id RESTART WITH 1;
ALTER TABLE review_likes ALTER COLUMN id RESTART WITH 1;
ALTER TABLE reviews ALTER COLUMN review_id RESTART WITH 1;
ALTER TABLE friendships ALTER COLUMN id RESTART WITH 1;

-- ALTER TABLE film_likes ALTER COLUMN id RESTART WITH 1;
-- ALTER TABLE film_genres ALTER COLUMN id RESTART WITH 1;

ALTER TABLE film_directors ALTER COLUMN id RESTART WITH 1;

ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1;
ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;
ALTER TABLE genres ALTER COLUMN genre_id RESTART WITH 1;
ALTER TABLE ratings ALTER COLUMN rating_id RESTART WITH 1;
ALTER TABLE directors ALTER COLUMN director_id RESTART WITH 1;

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
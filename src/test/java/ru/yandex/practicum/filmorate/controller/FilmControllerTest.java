package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ru.yandex.practicum.filmorate.adapter.LocalDateAdapter;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Тестовый класс для контроллера фильмов
@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;
    // Gson
    private final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
    // Путь
    private static final String FILMS_URL = "/films";

    // Очистка мапы контроллера через метод DELETE
    @BeforeEach
    void reset() throws Exception {
        mockMvc.perform(delete(FILMS_URL + "/clear"));
    }

    // Проверяет добавление нового фильма
    @Test
    void shouldAddNewValidFilm() throws Exception {
        final Film validFilm = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .build();

        mockMvc.perform(post(FILMS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(validFilm)))
                .andExpect(status().isOk());

        mockMvc.perform(get(FILMS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // Проверяет попытку добавления нового фильма с некорректным именем
    @Test
    void shouldNotAddFilmWithInvalidName() throws Exception {
        final Film invalidFilmWithNullName = Film.builder()
                .description("description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .build();

        mockMvc.perform(post(FILMS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(invalidFilmWithNullName)))
                .andExpect(status().isBadRequest());

        final Film invalidFilmWithEmptyName = Film.builder()
                .name("")
                .description("description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .build();

        mockMvc.perform(post(FILMS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(invalidFilmWithEmptyName)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get(FILMS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // Проверяет попытку добавления нового фильма с некорректным описанием
    @Test
    void shouldNotAddFilmWithInvalidDescription() throws Exception {
        final String invalidDescription = "a".repeat(201);

        final Film invalidFilm = Film.builder()
                .name("name")
                .description(invalidDescription)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .build();

        mockMvc.perform(post(FILMS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(invalidFilm)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get(FILMS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // Проверяет попытку добавления нового фильма с некорректной датой релиза
    @Test
    void shouldNotAddFilmWithInvalidReleaseDate() throws Exception {
        final Film invalidFilm = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1776, 7, 4))
                .duration(100)
                .build();

        mockMvc.perform(post(FILMS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(invalidFilm)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get(FILMS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // Проверяет попытку добавления нового фильма с некорректной длительностью
    @Test
    void shouldNotAddFilmWithIncorrectDuration() throws Exception {
        final Film invalidFilmWithZeroDuration = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(0)
                .build();

        mockMvc.perform(post(FILMS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(invalidFilmWithZeroDuration)))
                .andExpect(status().isBadRequest());

        final Film invalidFilmWithNegativeDuration = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(-100)
                .build();

        mockMvc.perform(post(FILMS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(invalidFilmWithNegativeDuration)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get(FILMS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // Проверяет попытку отправки POST-запроса с пустым body
    @Test
    void shouldHandleEmptyRequest() throws Exception {
        mockMvc.perform(post(FILMS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    // Проверяет получение всех фильмов
    @Test
    void shouldGetAllFilms() throws Exception {
        fillWithValidData();

        mockMvc.perform(get(FILMS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    // Проверяет обновление существующего фильма
    @Test
    void shouldUpdateFilm() throws Exception {
        fillWithValidData();

        Film film1 = Film.builder()
                .id(1)
                .name("new name 1")
                .description("new desc 1")
                .releaseDate(LocalDate.of(2010, 10, 10))
                .duration(1000)
                .build();

        mockMvc.perform(put(FILMS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(film1)))
                .andExpect(status().isOk());
    }

    // Проверяет обновление несуществующего фильма
    @Test
    void shouldNotUpdateUnknownFilm() throws Exception {
        fillWithValidData();

        Film film1 = Film.builder()
                .id(100)
                .name("new name 1")
                .description("new desc 1")
                .releaseDate(LocalDate.of(2010, 10, 10))
                .duration(1000)
                .build();

        mockMvc.perform(put(FILMS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(film1)))
                .andExpect(status().isNotFound());
    }

    // Заполняет мапу контроллера тестовыми валидными данными
    void fillWithValidData() throws Exception {
        Film film1 = Film.builder()
                .name("name 1")
                .description("desc 1")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .build();
        Film film2 = Film.builder()
                .name("name 2")
                .description("desc 2")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .build();
        Film film3 = Film.builder()
                .name("name 3")
                .description("desc 3")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .build();
        List<Film> films = List.of(film1, film2, film3);

        for (Film film : films) {
            mockMvc.perform(post(FILMS_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(film)))
                    .andExpect(status().isOk());
        }
    }
}
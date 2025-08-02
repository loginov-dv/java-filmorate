package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.adapter.LocalDateAdapter;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    // Очистка хранилища фильмов через метод DELETE
    @BeforeEach
    void reset() throws Exception {
        mockMvc.perform(delete(FILMS_URL + "/clear"));
        mockMvc.perform(delete("/users/clear"));
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

    // Проверяет добавление лайка
    @Test
    void shouldPutLike() throws Exception {
        fillWithValidData();
        addUsers();

        mockMvc.perform(put(FILMS_URL + "/1/like/1"))
                .andExpect(status().isOk());

        mockMvc.perform(get(FILMS_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes", hasSize(1)));
    }

    // Проверяет удаление лайка
    @Test
    void shouldRemoveLike() throws Exception {
        fillWithValidData();
        addUsers();

        mockMvc.perform(put(FILMS_URL + "/1/like/1"))
                .andExpect(status().isOk());
        mockMvc.perform(delete(FILMS_URL + "/1/like/1"))
                .andExpect(status().isOk());

        mockMvc.perform(get(FILMS_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes", hasSize(0)));
    }

    // Проверяет попытку добавления лайка несуществующему фильму
    @Test
    void shouldReturnNotFoundWhenRequestedPutLikeForFilmThatDoesntExist() throws Exception {
        fillWithValidData();
        addUsers();

        mockMvc.perform(put(FILMS_URL + "/4/like/1"))
                .andExpect(status().isNotFound());
    }

    // Проверяет попытку добавления лайка от несуществующего пользователя
    @Test
    void shouldReturnNotFoundWhenRequestedPutLikeFromUserThatDoesntExist() throws Exception {
        fillWithValidData();
        addUsers();

        mockMvc.perform(put(FILMS_URL + "/1/like/4"))
                .andExpect(status().isNotFound());
    }

    // Проверяет получение популярных фильмов
    @Test
    void shouldReturnPopularFilms() throws Exception {
        fillWithValidData();
        addUsers();

        mockMvc.perform(put(FILMS_URL + "/1/like/1"))
                .andExpect(status().isOk());
        mockMvc.perform(put(FILMS_URL + "/1/like/2"))
                .andExpect(status().isOk());
        mockMvc.perform(put(FILMS_URL + "/1/like/3"))
                .andExpect(status().isOk());

        mockMvc.perform(put(FILMS_URL + "/2/like/1"))
                .andExpect(status().isOk());
        mockMvc.perform(put(FILMS_URL + "/2/like/2"))
                .andExpect(status().isOk());

        mockMvc.perform(put(FILMS_URL + "/3/like/1"))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get(FILMS_URL + "/popular"))
                .andExpect(status().isOk())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        TypeToken<List<Film>> typeToken = new TypeToken<>(){};
        List<Film> popularFilms = gson.fromJson(json, typeToken.getType());

        assertNotNull(popularFilms);
        assertEquals(3, popularFilms.size());
        assertEquals(3, popularFilms.get(0).getLikes().size());
        assertEquals(2, popularFilms.get(1).getLikes().size());
        assertEquals(1, popularFilms.get(2).getLikes().size());
    }

    // Заполняет хранилище фильмов тестовыми валидными данными
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

    // Заполняет хранилище пользователей
    void addUsers() throws Exception {
        final User user1 = User.builder()
                .name("user1")
                .login("user1")
                .email("user1@test.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        final User user2 = User.builder()
                .name("user2")
                .login("user2")
                .email("user2@test.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        final User user3 = User.builder()
                .name("user3")
                .login("user3")
                .email("user3@test.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        List<User> users = List.of(user1, user2, user3);

        for (User user : users) {
            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(user)))
                    .andExpect(status().isOk());
        }
    }
}
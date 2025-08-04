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
        /*final Film validFilm = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .build();*/
        Film validFilm = new Film();
        validFilm.setName("name");
        validFilm.setDescription("description");
        validFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        validFilm.setDuration(100);

        mockMvc.perform(post(FILMS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(validFilm)))
                .andExpect(status().isCreated());
        MvcResult result = mockMvc.perform(get(FILMS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeToken<List<Film>> typeToken = new TypeToken<>(){};
        List<Film> films = gson.fromJson(json, typeToken.getType());
        Film responseFilm = films.getFirst();
        assertEquals(validFilm.getName(), responseFilm.getName(), "Не совпадают имена");
        assertEquals(validFilm.getDescription(), responseFilm.getDescription(), "Не совпадают описания");
        assertEquals(validFilm.getReleaseDate(), responseFilm.getReleaseDate(), "Не совпадают даты релиза");
        assertEquals(validFilm.getDuration(), responseFilm.getDuration(), "Не совпадают продолжительности");
    }

    // Проверяет попытку добавления нового фильма с некорректным именем
    @Test
    void shouldNotAddFilmWithInvalidName() throws Exception {
        /*final Film invalidFilmWithNullName = Film.builder()
                .description("description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .build();*/
        final Film invalidFilmWithNullName = new Film();
        invalidFilmWithNullName.setDescription("description");
        invalidFilmWithNullName.setReleaseDate(LocalDate.of(2000, 1, 1));
        invalidFilmWithNullName.setDuration(100);

        mockMvc.perform(post(FILMS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(invalidFilmWithNullName)))
                .andExpect(status().isBadRequest());

        /*final Film invalidFilmWithEmptyName = Film.builder()
                .name("")
                .description("description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .build();*/
        final Film invalidFilmWithEmptyName = new Film();
        invalidFilmWithEmptyName.setName("");
        invalidFilmWithEmptyName.setDescription("description");
        invalidFilmWithEmptyName.setReleaseDate(LocalDate.of(2000, 1, 1));
        invalidFilmWithEmptyName.setDuration(100);

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

        /*final Film invalidFilm = Film.builder()
                .name("name")
                .description(invalidDescription)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .build();*/
        final Film invalidFilm = new Film();
        invalidFilm.setName("name");
        invalidFilm.setDescription(invalidDescription);
        invalidFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        invalidFilm.setDuration(100);

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
        /*final Film invalidFilm = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1776, 7, 4))
                .duration(100)
                .build();*/
        final Film invalidFilm = new Film();
        invalidFilm.setName("name");
        invalidFilm.setDescription("description");
        invalidFilm.setReleaseDate(LocalDate.of(1776, 7, 4));
        invalidFilm.setDuration(100);

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
        /*final Film invalidFilmWithZeroDuration = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(0)
                .build();*/
        final Film invalidFilmWithZeroDuration = new Film();
        invalidFilmWithZeroDuration.setName("name");
        invalidFilmWithZeroDuration.setDescription("description");
        invalidFilmWithZeroDuration.setReleaseDate(LocalDate.of(2000, 1, 1));
        invalidFilmWithZeroDuration.setDuration(0);

        mockMvc.perform(post(FILMS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(invalidFilmWithZeroDuration)))
                .andExpect(status().isBadRequest());

        /*final Film invalidFilmWithNegativeDuration = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(-100)
                .build();*/
        final Film invalidFilmWithNegativeDuration = new Film();
        invalidFilmWithNegativeDuration.setName("name");
        invalidFilmWithNegativeDuration.setDescription("description");
        invalidFilmWithNegativeDuration.setReleaseDate(LocalDate.of(2000, 1, 1));
        invalidFilmWithNegativeDuration.setDuration(-100);

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
        // TODO: можно обрабатывать HttpMessageNotReadableException и возвращать 400
        mockMvc.perform(post(FILMS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isInternalServerError());
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

        /*Film film = Film.builder()
                .id(1)
                .name("new name 1")
                .description("new desc 1")
                .releaseDate(LocalDate.of(2010, 10, 10))
                .duration(1000)
                .build();*/
        Film film = new Film();
        film.setId(1);
        film.setName("new name 1");
        film.setDescription("new desc 1");
        film.setReleaseDate(LocalDate.of(2010, 10, 10));
        film.setDuration(1000);

        MvcResult result = mockMvc.perform(put(FILMS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(film)))
                .andExpect(status().isOk())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        Film responseFilm = gson.fromJson(json, Film.class);
        assertEquals(film.getName(), responseFilm.getName(), "Не совпадают имена");
        assertEquals(film.getDescription(), responseFilm.getDescription(), "Не совпадают описания");
        assertEquals(film.getReleaseDate(), responseFilm.getReleaseDate(), "Не совпадают даты релиза");
        assertEquals(film.getDuration(), responseFilm.getDuration(), "Не совпадают продолжительности");
    }

    // Проверяет обновление несуществующего фильма
    @Test
    void shouldNotUpdateUnknownFilm() throws Exception {
        fillWithValidData();

        /*Film film1 = Film.builder()
                .id(100)
                .name("new name 1")
                .description("new desc 1")
                .releaseDate(LocalDate.of(2010, 10, 10))
                .duration(1000)
                .build();*/
        Film film1 = new Film();
        film1.setId(100);
        film1.setName("new name 1");
        film1.setDescription("new desc 1");
        film1.setReleaseDate(LocalDate.of(2010, 10, 10));
        film1.setDuration(1000);

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
        /*Film film1 = Film.builder()
                .name("name 1")
                .description("desc 1")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .build();*/
        Film film1 = new Film();
        film1.setName("name 1");
        film1.setDescription("desc 1");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(100);
        /*Film film2 = Film.builder()
                .name("name 2")
                .description("desc 2")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .build();*/
        Film film2 = new Film();
        film2.setName("name 2");
        film2.setDescription("desc 2");
        film2.setReleaseDate(LocalDate.of(2000, 1, 1));
        film2.setDuration(100);
        /*Film film3 = Film.builder()
                .name("name 3")
                .description("desc 3")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .build();*/
        Film film3 = new Film();
        film3.setName("name 3");
        film3.setDescription("desc 3");
        film3.setReleaseDate(LocalDate.of(2000, 1, 1));
        film3.setDuration(100);
        List<Film> films = List.of(film1, film2, film3);

        for (Film film : films) {
            mockMvc.perform(post(FILMS_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(film)))
                    .andExpect(status().isCreated());
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
                    .andExpect(status().isCreated());
        }
    }
}
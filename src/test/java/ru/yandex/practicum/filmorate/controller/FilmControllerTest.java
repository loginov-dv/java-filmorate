package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.adapter.LocalDateAdapter;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.MpaIdDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Тестовый класс для контроллера фильмов
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Sql(scripts = { "/schema.sql", "/data.sql", "/test-data.sql" })
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;
    // Gson
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();
    // Путь
    private static final String FILMS_URL = "/films";

    // Проверяет добавление нового фильма
    @Test
    @Order(6)
    void shouldAddNewValidFilm() throws Exception {
        NewFilmRequest request = new NewFilmRequest();
        request.setName("name");
        request.setDescription("description");
        request.setReleaseDate(LocalDate.of(2000, 1, 1));
        request.setDuration(100);

        MpaIdDto mpaIdDto = new MpaIdDto();
        mpaIdDto.setId(1);

        request.setMpa(mpaIdDto);

        MvcResult response = mockMvc.perform(post(FILMS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String json = response.getResponse().getContentAsString();
        FilmDto filmDto = gson.fromJson(json, FilmDto.class);
        assertEquals(6, filmDto.getId(), "Не совпадают id");
        assertEquals(request.getName(), filmDto.getName(), "Не совпадают имена");
        assertEquals(request.getDescription(), filmDto.getDescription(), "Не совпадают описания");
        assertEquals(request.getReleaseDate(), filmDto.getReleaseDate(), "Не совпадают даты релиза");
        assertEquals(request.getDuration(), filmDto.getDuration(), "Не совпадают продолжительности");
    }

    // Проверяет попытку добавления нового фильма с некорректным именем
    @Test
    @Order(2)
    void shouldNotAddFilmWithInvalidName() throws Exception {
        NewFilmRequest invalidFilmWithNullName = new NewFilmRequest();
        invalidFilmWithNullName.setDescription("description");
        invalidFilmWithNullName.setReleaseDate(LocalDate.of(2000, 1, 1));
        invalidFilmWithNullName.setDuration(100);

        MpaIdDto mpaIdDto = new MpaIdDto();
        mpaIdDto.setId(1);

        invalidFilmWithNullName.setMpa(mpaIdDto);

        mockMvc.perform(post(FILMS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(invalidFilmWithNullName)))
                .andExpect(status().isBadRequest());

        NewFilmRequest invalidFilmWithEmptyName = new NewFilmRequest();
        invalidFilmWithEmptyName.setName("");
        invalidFilmWithEmptyName.setDescription("description");
        invalidFilmWithEmptyName.setReleaseDate(LocalDate.of(2000, 1, 1));
        invalidFilmWithEmptyName.setDuration(100);
        invalidFilmWithEmptyName.setMpa(mpaIdDto);

        mockMvc.perform(post(FILMS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(invalidFilmWithEmptyName)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get(FILMS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));
    }

    // Проверяет попытку добавления нового фильма с некорректным описанием
    @Test
    @Order(3)
    void shouldNotAddFilmWithInvalidDescription() throws Exception {
        final String invalidDescription = "a".repeat(201);
        NewFilmRequest invalidFilm = new NewFilmRequest();
        invalidFilm.setName("name");
        invalidFilm.setDescription(invalidDescription);
        invalidFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        invalidFilm.setDuration(100);

        MpaIdDto mpaIdDto = new MpaIdDto();
        mpaIdDto.setId(1);

        invalidFilm.setMpa(mpaIdDto);

        mockMvc.perform(post(FILMS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(invalidFilm)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get(FILMS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));
    }

    // Проверяет попытку добавления нового фильма с некорректной датой релиза
    @Test
    @Order(4)
    void shouldNotAddFilmWithInvalidReleaseDate() throws Exception {
        NewFilmRequest invalidFilm = new NewFilmRequest();
        invalidFilm.setName("name");
        invalidFilm.setDescription("description");
        invalidFilm.setReleaseDate(LocalDate.of(1776, 7, 4));
        invalidFilm.setDuration(100);

        MpaIdDto mpaIdDto = new MpaIdDto();
        mpaIdDto.setId(1);

        invalidFilm.setMpa(mpaIdDto);

        mockMvc.perform(post(FILMS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(invalidFilm)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get(FILMS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));
    }

    // Проверяет попытку добавления нового фильма с некорректной длительностью
    @Test
    @Order(5)
    void shouldNotAddFilmWithIncorrectDuration() throws Exception {
        NewFilmRequest invalidFilmWithZeroDuration = new NewFilmRequest();
        invalidFilmWithZeroDuration.setName("name");
        invalidFilmWithZeroDuration.setDescription("description");
        invalidFilmWithZeroDuration.setReleaseDate(LocalDate.of(2000, 1, 1));
        invalidFilmWithZeroDuration.setDuration(0);

        MpaIdDto mpaIdDto = new MpaIdDto();
        mpaIdDto.setId(1);

        invalidFilmWithZeroDuration.setMpa(mpaIdDto);

        mockMvc.perform(post(FILMS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(invalidFilmWithZeroDuration)))
                .andExpect(status().isBadRequest());

        NewFilmRequest invalidFilmWithNegativeDuration = new NewFilmRequest();
        invalidFilmWithNegativeDuration.setName("name");
        invalidFilmWithNegativeDuration.setDescription("description");
        invalidFilmWithNegativeDuration.setReleaseDate(LocalDate.of(2000, 1, 1));
        invalidFilmWithNegativeDuration.setDuration(-100);

        invalidFilmWithNegativeDuration.setMpa(mpaIdDto);

        mockMvc.perform(post(FILMS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(invalidFilmWithNegativeDuration)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get(FILMS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));
    }

    // Проверяет попытку отправки POST-запроса с пустым body
    @Test
    void shouldHandleEmptyRequest() throws Exception {
        mockMvc.perform(post(FILMS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isInternalServerError());
    }

    // Проверяет получение всех фильмов
    @Test
    @Order(1)
    void shouldGetAllFilms() throws Exception {
        mockMvc.perform(get(FILMS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    void shouldGetFilmById() throws Exception {
        mockMvc.perform(get(FILMS_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    // Проверяет обновление существующего фильма
    @Test
    void shouldUpdateFilm() throws Exception {
        UpdateFilmRequest request = new UpdateFilmRequest();
        request.setId(1);
        request.setName("new name 1");
        request.setDescription("new desc 1");
        request.setReleaseDate(LocalDate.of(2010, 10, 10));
        request.setDuration(1000);

        MvcResult response = mockMvc.perform(put(FILMS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(request)))
                .andExpect(status().isOk())
                .andReturn();

        String json = response.getResponse().getContentAsString();
        FilmDto filmDto = gson.fromJson(json, FilmDto.class);
        assertEquals(1, filmDto.getId(), "Не совпадают id");
        assertEquals(request.getName(), filmDto.getName(), "Не совпадают имена");
        assertEquals(request.getDescription(), filmDto.getDescription(), "Не совпадают описания");
        assertEquals(request.getReleaseDate(), filmDto.getReleaseDate(), "Не совпадают даты релиза");
        assertEquals(request.getDuration(), filmDto.getDuration(), "Не совпадают продолжительности");
    }

    // Проверяет обновление несуществующего фильма
    @Test
    void shouldNotUpdateUnknownFilm() throws Exception {
        UpdateFilmRequest request = new UpdateFilmRequest();
        request.setId(100);
        request.setName("new name 1");
        request.setDescription("new desc 1");
        request.setReleaseDate(LocalDate.of(2010, 10, 10));
        request.setDuration(1000);

        mockMvc.perform(put(FILMS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(request)))
                .andExpect(status().isNotFound());
    }

    // Проверяет добавление лайка
    @Test
    void shouldPutLike() throws Exception {
        mockMvc.perform(put(FILMS_URL + "/1/like/1"))
                .andExpect(status().isOk());

        /*mockMvc.perform(get(FILMS_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes", hasSize(1)));*/
    }

    // Проверяет удаление лайка
    @Test
    void shouldRemoveLike() throws Exception {
        mockMvc.perform(put(FILMS_URL + "/1/like/1"))
                .andExpect(status().isOk());
        mockMvc.perform(delete(FILMS_URL + "/1/like/1"))
                .andExpect(status().isOk());

        /*mockMvc.perform(get(FILMS_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes", hasSize(0)));*/
    }

    // Проверяет попытку добавления лайка несуществующему фильму
    @Test
    void shouldReturnNotFoundWhenRequestedPutLikeForFilmThatDoesntExist() throws Exception {
        mockMvc.perform(put(FILMS_URL + "/100/like/1"))
                .andExpect(status().isNotFound());
    }

    // Проверяет попытку добавления лайка от несуществующего пользователя
    @Test
    void shouldReturnNotFoundWhenRequestedPutLikeFromUserThatDoesntExist() throws Exception {
        mockMvc.perform(put(FILMS_URL + "/1/like/100"))
                .andExpect(status().isNotFound());
    }

    // Проверяет получение популярных фильмов
    @Test
    void shouldReturnPopularFilms() throws Exception {
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
    }
}
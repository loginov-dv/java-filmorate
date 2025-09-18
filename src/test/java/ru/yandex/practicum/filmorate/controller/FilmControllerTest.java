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
import org.springframework.jdbc.core.JdbcTemplate;
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

    @Autowired
    private JdbcTemplate jdbc;

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

    // helper: получить id фильма по имени
    private int getFilmIdByName(String name) {
        return jdbc.queryForObject("SELECT film_id FROM films WHERE name = ?", Integer.class, name);
    }

    // NEW: если режиссёр с таким именем уже есть — используем его id, иначе создаём
    private int ensureDirector(String name) {
        List<Integer> ids = jdbc.query(
                "SELECT director_id FROM directors WHERE name = ? ORDER BY director_id",
                (rs, rn) -> rs.getInt(1),
                name
        );
        if (!ids.isEmpty()) {
            return ids.get(0);
        }
        jdbc.update("INSERT INTO directors(name) VALUES (?)", name);
        return jdbc.queryForObject(
                "SELECT director_id FROM directors WHERE name = ? ORDER BY director_id DESC LIMIT 1",
                Integer.class,
                name
        );
    }

    // FIX: привязка фильма к режиссёру без дублирования режиссёра
    private void linkDirectorToFilm(int filmId, String directorName) {
        int directorId = ensureDirector(directorName);
        jdbc.update("INSERT INTO film_directors(film_id, director_id) VALUES (?,?)", filmId, directorId);
    }

    @Test
    void shouldSearchByTitle_sortedByLikes() throws Exception {
        // создаём 3 уникальных фильма с подстрокой 'searchTitle'
        MpaIdDto mpa = new MpaIdDto(); mpa.setId(1);

        NewFilmRequest a = new NewFilmRequest();
        a.setName("searchTitle_A");
        a.setDescription("d"); a.setReleaseDate(LocalDate.of(2010,1,1)); a.setDuration(100); a.setMpa(mpa);
        NewFilmRequest b = new NewFilmRequest();
        b.setName("searchTitle_B");
        b.setDescription("d"); b.setReleaseDate(LocalDate.of(2011,1,1)); b.setDuration(100); b.setMpa(mpa);
        NewFilmRequest c = new NewFilmRequest();
        c.setName("searchTitle_C");
        c.setDescription("d"); c.setReleaseDate(LocalDate.of(2012,1,1)); c.setDuration(100); c.setMpa(mpa);

        mockMvc.perform(post(FILMS_URL).contentType(MediaType.APPLICATION_JSON).content(gson.toJson(a))).andExpect(status().isCreated());
        mockMvc.perform(post(FILMS_URL).contentType(MediaType.APPLICATION_JSON).content(gson.toJson(b))).andExpect(status().isCreated());
        mockMvc.perform(post(FILMS_URL).contentType(MediaType.APPLICATION_JSON).content(gson.toJson(c))).andExpect(status().isCreated());

        int aId = getFilmIdByName("searchTitle_A");
        int bId = getFilmIdByName("searchTitle_B");
        int cId = getFilmIdByName("searchTitle_C");

        // лайки: A=2, B=1, C=0
        jdbc.update("INSERT INTO film_likes(film_id, user_id) VALUES (?,?)", aId, 1);
        jdbc.update("INSERT INTO film_likes(film_id, user_id) VALUES (?,?)", aId, 2);
        jdbc.update("INSERT INTO film_likes(film_id, user_id) VALUES (?,?)", bId, 1);

        MvcResult res = mockMvc.perform(get(FILMS_URL + "/search")
                        .param("query", "searchTitle")
                        .param("by", "title"))
                .andExpect(status().isOk())
                .andReturn();

        TypeToken<List<FilmDto>> tt = new TypeToken<>() {};
        List<FilmDto> list = gson.fromJson(res.getResponse().getContentAsString(), tt.getType());
        assertNotNull(list);
        // фильтруем только наши (по префиксу), чтобы не зависеть от тестовых данных
        List<FilmDto> ours = list.stream().filter(f -> f.getName().startsWith("searchTitle_")).toList();
        assertEquals(3, ours.size());
        assertEquals("searchTitle_A", ours.get(0).getName());
        assertEquals("searchTitle_B", ours.get(1).getName());
        assertEquals("searchTitle_C", ours.get(2).getName());
    }

    @Test
    void shouldSearchByDirector_sortedByLikes() throws Exception {
        // создаём 2 фильма без подстроки в названии
        MpaIdDto mpa = new MpaIdDto(); mpa.setId(1);

        NewFilmRequest f1 = new NewFilmRequest();
        f1.setName("X1_for_director_search");
        f1.setDescription("d"); f1.setReleaseDate(LocalDate.of(2013,1,1)); f1.setDuration(100); f1.setMpa(mpa);

        NewFilmRequest f2 = new NewFilmRequest();
        f2.setName("X2_for_director_search");
        f2.setDescription("d"); f2.setReleaseDate(LocalDate.of(2014,1,1)); f2.setDuration(100); f2.setMpa(mpa);

        mockMvc.perform(post(FILMS_URL).contentType(MediaType.APPLICATION_JSON).content(gson.toJson(f1))).andExpect(status().isCreated());
        mockMvc.perform(post(FILMS_URL).contentType(MediaType.APPLICATION_JSON).content(gson.toJson(f2))).andExpect(status().isCreated());

        int id1 = getFilmIdByName("X1_for_director_search");
        int id2 = getFilmIdByName("X2_for_director_search");

        // режиссёр, в имени которого есть 'searchDirector'
        linkDirectorToFilm(id1, "Director searchDirector One");
        linkDirectorToFilm(id2, "Director searchDirector One");

        // лайки: id1=1, id2=0
        jdbc.update("INSERT INTO film_likes(film_id, user_id) VALUES (?,?)", id1, 1);

        MvcResult res = mockMvc.perform(get(FILMS_URL + "/search")
                        .param("query", "searchDirector")
                        .param("by", "director"))
                .andExpect(status().isOk())
                .andReturn();

        TypeToken<List<FilmDto>> tt = new TypeToken<>() {};
        List<FilmDto> list = gson.fromJson(res.getResponse().getContentAsString(), tt.getType());
        assertNotNull(list);
        // выбираем только наши фильмы X1/X2
        List<FilmDto> ours = list.stream().filter(f -> f.getName().endsWith("_for_director_search")).toList();
        assertEquals(2, ours.size());
        assertEquals("X1_for_director_search", ours.get(0).getName()); // у него 1 лайк
        assertEquals("X2_for_director_search", ours.get(1).getName()); // 0 лайков
    }

    @Test
    void shouldSearchByDirectorAndTitle_noDuplicates_sortedByLikes() throws Exception {
        // готовим 3 фильма под уникальный запрос 'searchCombo'
        MpaIdDto mpa = new MpaIdDto(); mpa.setId(1);

        NewFilmRequest both = new NewFilmRequest();
        both.setName("searchCombo_both");
        both.setDescription("d"); both.setReleaseDate(LocalDate.of(2015,1,1)); both.setDuration(100); both.setMpa(mpa);

        NewFilmRequest onlyTitle = new NewFilmRequest();
        onlyTitle.setName("searchCombo_title_only");
        onlyTitle.setDescription("d"); onlyTitle.setReleaseDate(LocalDate.of(2016,1,1)); onlyTitle.setDuration(100); onlyTitle.setMpa(mpa);

        NewFilmRequest onlyDirector = new NewFilmRequest();
        onlyDirector.setName("Other_for_cmb");
        onlyDirector.setDescription("d"); onlyDirector.setReleaseDate(LocalDate.of(2017,1,1)); onlyDirector.setDuration(100); onlyDirector.setMpa(mpa);

        mockMvc.perform(post(FILMS_URL).contentType(MediaType.APPLICATION_JSON).content(gson.toJson(both))).andExpect(status().isCreated());
        mockMvc.perform(post(FILMS_URL).contentType(MediaType.APPLICATION_JSON).content(gson.toJson(onlyTitle))).andExpect(status().isCreated());
        mockMvc.perform(post(FILMS_URL).contentType(MediaType.APPLICATION_JSON).content(gson.toJson(onlyDirector))).andExpect(status().isCreated());

        int idBoth = getFilmIdByName("searchCombo_both");
        int idTitle = getFilmIdByName("searchCombo_title_only");
        int idDir = getFilmIdByName("Other_for_cmb");

        // режиссёры: у двух из них имя содержит 'searchCombo'
        linkDirectorToFilm(idBoth, "Director searchCombo Hit");
        linkDirectorToFilm(idTitle, "Director Other");
        linkDirectorToFilm(idDir, "searchCombo Director Only");

        // лайки: both=3, title=2, dir=1
        jdbc.update("INSERT INTO film_likes(film_id, user_id) VALUES (?,?)", idBoth, 1);
        jdbc.update("INSERT INTO film_likes(film_id, user_id) VALUES (?,?)", idBoth, 2);
        jdbc.update("INSERT INTO film_likes(film_id, user_id) VALUES (?,?)", idBoth, 3);

        jdbc.update("INSERT INTO film_likes(film_id, user_id) VALUES (?,?)", idTitle, 1);
        jdbc.update("INSERT INTO film_likes(film_id, user_id) VALUES (?,?)", idTitle, 2);

        jdbc.update("INSERT INTO film_likes(film_id, user_id) VALUES (?,?)", idDir, 1);

        MvcResult res = mockMvc.perform(get(FILMS_URL + "/search")
                        .param("query", "searchCombo")
                        .param("by", "director,title"))
                .andExpect(status().isOk())
                .andReturn();

        TypeToken<List<FilmDto>> tt = new TypeToken<>() {};
        List<FilmDto> list = gson.fromJson(res.getResponse().getContentAsString(), tt.getType());
        assertNotNull(list);
        // фильтруем наши 3 фильма
        List<FilmDto> ours = list.stream()
                .filter(f -> f.getName().equals("searchCombo_both")
                        || f.getName().equals("searchCombo_title_only")
                        || f.getName().equals("Other_for_cmb"))
                .toList();
        assertEquals(3, ours.size());
        assertEquals("searchCombo_both",  ours.get(0).getName());      // 3 лайка
        assertEquals("searchCombo_title_only", ours.get(1).getName()); // 2 лайка
        assertEquals("Other_for_cmb", ours.get(2).getName());          // 1 лайк
    }

    @Test
    void shouldReturnBadRequestOnBlankQuery() throws Exception {
        mockMvc.perform(get(FILMS_URL + "/search")
                        .param("query", "   ")
                        .param("by", "title"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestOnUnsupportedBy() throws Exception {
        mockMvc.perform(get(FILMS_URL + "/search")
                        .param("query", "searchTitle")
                        .param("by", "foo"))
                .andExpect(status().isBadRequest());
    }
}

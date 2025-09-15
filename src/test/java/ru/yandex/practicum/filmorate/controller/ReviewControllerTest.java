package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(statements = {
        // Чистим и подготавливаем минимальные данные: 2 пользователя и 1 фильм
        "DELETE FROM review_likes",
        "DELETE FROM reviews",
        "DELETE FROM film_genres",
        "DELETE FROM films",
        "DELETE FROM users",
        // Вставляем предсказуемые id
        "INSERT INTO users(user_id, email, login, name, birthday) VALUES (1,'u1@mail','u1','User1','1990-01-01')",
        "INSERT INTO users(user_id, email, login, name, birthday) VALUES (2,'u2@mail','u2','User2','1990-01-01')",
        "INSERT INTO films(film_id, name, description, release_date, duration, rating_id) " +
                "VALUES (1,'Film #1','Desc','2000-01-01',120,1)"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ReviewControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper om;

    @Test
    @DisplayName("POST /reviews — создаёт отзыв с useful=0 и возвращает 201")
    void createReview_returns201() throws Exception {
        String body = """
            {
              "content":"Nice movie",
              "isPositive":true,
              "userId":1,
              "filmId":1
            }
            """;

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reviewId", notNullValue()))
                .andExpect(jsonPath("$.content", is("Nice movie")))
                .andExpect(jsonPath("$.isPositive", is(true)))
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.filmId", is(1)))
                .andExpect(jsonPath("$.useful", is(0)));
    }

    @Test
    @DisplayName("PUT /reviews — обновляет текст/тональность")
    void updateReview_updatesFields() throws Exception {
        // создаём
        String create = """
            {
              "content":"Old",
              "isPositive":true,
              "userId":1,
              "filmId":1
            }
            """;
        String created = mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(create))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int id = om.readTree(created).get("reviewId").asInt();

        // апдейтим
        String update = """
            {
              "reviewId": %d,
              "content":"New",
              "isPositive": false
            }
            """.formatted(id);

        mockMvc.perform(put("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(update))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId", is(id)))
                .andExpect(jsonPath("$.content", is("New")))
                .andExpect(jsonPath("$.isPositive", is(false)));
    }

    @Test
    @DisplayName("PUT/DELETE like|dislike — корректно меняют рейтинг useful")
    void reactions_changeUseful() throws Exception {
        String created = mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"content":"X","isPositive":true,"userId":1,"filmId":1}
                        """))
                .andReturn().getResponse().getContentAsString();
        int id = om.readTree(created).get("reviewId").asInt();

        mockMvc.perform(put("/reviews/{id}/like/{userId}", id, 1))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/reviews/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.useful", is(1)));

        // смена на dislike тем же user=1: +1 -> -1 (дельта -2)
        mockMvc.perform(put("/reviews/{id}/dislike/{userId}", id, 1))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/reviews/{id}", id))
                .andExpect(jsonPath("$.useful", is(-1)));

        // удаляем дизлайк: -1 -> 0 (дельта +1)
        mockMvc.perform(delete("/reviews/{id}/dislike/{userId}", id, 1))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/reviews/{id}", id))
                .andExpect(jsonPath("$.useful", is(0)));
    }

    @Test
    @DisplayName("GET /reviews?filmId=... — сортирует по useful по убыванию")
    void listByFilm_sortedByUsefulDesc() throws Exception {
        // r1
        int r1 = om.readTree(
                mockMvc.perform(post("/reviews")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {"content":"A","isPositive":true,"userId":1,"filmId":1}
                                """))
                        .andReturn().getResponse().getContentAsString()
        ).get("reviewId").asInt();

        // r2
        int r2 = om.readTree(
                mockMvc.perform(post("/reviews")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {"content":"B","isPositive":false,"userId":2,"filmId":1}
                                """))
                        .andReturn().getResponse().getContentAsString()
        ).get("reviewId").asInt();

        // Подкручиваем полезность: r1(+1), r2(-1)
        mockMvc.perform(put("/reviews/{id}/like/{userId}", r1, 2)).andExpect(status().isNoContent());
        mockMvc.perform(put("/reviews/{id}/dislike/{userId}", r2, 1)).andExpect(status().isNoContent());

        // Ожидаем: r1 (useful=+1) первым, r2 (useful=-1) вторым
        mockMvc.perform(get("/reviews").param("filmId", "1").param("count", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reviewId", is(r1)))
                .andExpect(jsonPath("$[1].reviewId", is(r2)));
    }

    @Test
    @DisplayName("POST /reviews — проваливается с 400 при пустом контенте")
    void createReview_validationError() throws Exception {
        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"content":"  ","isPositive":true,"userId":1,"filmId":1}
                        """))
                .andExpect(status().isBadRequest());
    }
}

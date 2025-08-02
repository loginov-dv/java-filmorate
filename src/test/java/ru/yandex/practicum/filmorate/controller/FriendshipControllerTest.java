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
import ru.yandex.practicum.filmorate.model.User;


import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Тестовый класс для контроллера дружбы пользователей
@SpringBootTest
@AutoConfigureMockMvc
class FriendshipControllerTest {
    @Autowired
    private MockMvc mockMvc;
    // Gson
    private final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();

    // Очистка хранилища через метод DELETE
    @BeforeEach
    void reset() throws Exception {
        mockMvc.perform(delete("/users/friends/clear"));
        mockMvc.perform(delete("/users/clear"));
        fillWithValidData();
    }

    // Проверяет добавление двух пользователей в друзья
    @Test
    void shouldAddToFriends() throws Exception {
        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isOk());
    }

    // Проверяет попытку добавления в друзья пользователя, которого не существует
    @Test
    void shouldReturnNotFoundIfOneOfUsersDoesntExist() throws Exception {
        mockMvc.perform(put("/users/1/friends/4"))
                .andExpect(status().isNotFound());
        mockMvc.perform(put("/users/4/friends/1"))
                .andExpect(status().isNotFound());
    }

    // Проверяет повторную попытку добавления в друзья
    @Test
    void shouldReturnBadRequestIfUsersAreAlreadyFriends() throws Exception {
        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isBadRequest());
    }

    // Проверяет удаление из друзей
    @Test
    void shouldRemoveFromFriends() throws Exception {
        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/users/1/friends/2"))
                .andExpect(status().isOk());
    }

    // Проверяет попытку удаления из друзей, когда пользователи не состояли в дружеской связи
    @Test
    void shouldReturnBadRequestIfUsersAreNotFriendsWhenRemovalFromFriendsRequested() throws Exception {
        mockMvc.perform(delete("/users/1/friends/2"))
                .andExpect(status().isBadRequest());
    }

    // Проверяет возвращение списка друзей
    @Test
    void shouldGetAllFriends() throws Exception {
        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/users/1/friends/3"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // Проверяет возвращение списка общих друзей
    @Test
    void shouldGetCommonFriends() throws Exception {
        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/users/3/friends/2"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/users/1/friends/common/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // Заполняет хранилище пользователей тестовыми валидными данными
    void fillWithValidData() throws Exception {
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
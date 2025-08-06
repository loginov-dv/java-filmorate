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
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Тестовый класс для контроллера пользователей
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    // Gson
    private final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
    // Путь
    private static final String USERS_URL = "/users";

    // Очистка хранилища через метод DELETE
    @BeforeEach
    void reset() throws Exception {
        mockMvc.perform(delete(USERS_URL + "/clear"));
        mockMvc.perform(delete(USERS_URL + "/friends/clear"));
    }

    // Проверяет добавление нового пользователя
    @Test
    void shouldAddNewValidUser() throws Exception {
        final User validUser = User.builder()
                .name("vasya")
                .login("vasya")
                .email("vasya@test.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        mockMvc.perform(post(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(validUser)))
                .andExpect(status().isCreated());

        MvcResult result = mockMvc.perform(get(USERS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeToken<List<User>> typeToken = new TypeToken<>(){};
        List<User> users = gson.fromJson(json, typeToken.getType());
        User responseUser = users.getFirst();
        assertEquals(validUser.getName(), responseUser.getName(), "Не совпадают имена");
        assertEquals(validUser.getLogin(), responseUser.getLogin(), "Не совпадают логины");
        assertEquals(validUser.getEmail(), responseUser.getEmail(), "Не совпадают email");
        assertEquals(validUser.getBirthday(), responseUser.getBirthday(), "Не совпадают даты рождения");
    }

    // Проверяет попытку добавления нового пользователя с некорректным логином
    @Test
    void shouldNotAddUserWithInvalidLogin() throws Exception {
        final User invalidUserWithNullLogin = User.builder()
                .name("petya")
                .email("petya@test.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        final User ivalidUserWithEmptyLogin = User.builder()
                .name("sasha")
                .login("")
                .email("sasha@test.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        mockMvc.perform(post(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(invalidUserWithNullLogin)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(ivalidUserWithEmptyLogin)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get(USERS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // Проверяет попытку добавления нового пользователя с логином, содержащим пробелы
    @Test
    void shouldNotAddUserWithWhitespaceInLogin() throws Exception {
        final User invalidUser1 = User.builder()
                .name("masha")
                .login("ma sha")
                .email("masha@test.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        final User invalidUser2 = User.builder()
                .name("masha")
                .login(" masha")
                .email("masha@test.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        final User invalidUser3 = User.builder()
                .name("masha")
                .login("masha ")
                .email("masha@test.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        final User invalidUser4 = User.builder()
                .name("masha")
                .login("mas     ha")
                .email("masha@test.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        final User invalidUser5 = User.builder()
                .name("masha")
                .login(" mas ha ")
                .email("masha@test.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        List<User> invalidUsers = List.of(invalidUser1, invalidUser2, invalidUser3, invalidUser4, invalidUser5);

        for (User invalidUser : invalidUsers) {
            mockMvc.perform(post(USERS_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(invalidUser)))
                    .andExpect(status().isBadRequest());
        }

        mockMvc.perform(get(USERS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // Проверяет попытку добавления нового пользователя с некорректной электронной почтой
    @Test
    void shouldNotAddUserWithInvalidEmail() throws Exception {
        final User invalidUserWithNullEmail = User.builder()
                .name("kolya")
                .login("kolya")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        final User ivalidUserWithEmptyEmail = User.builder()
                .name("nastya")
                .login("nastya")
                .email("")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        final User invalidUserWithMissingSymbol = User.builder()
                .name("olya")
                .login("olya")
                .email("olya")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        mockMvc.perform(post(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(invalidUserWithNullEmail)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(ivalidUserWithEmptyEmail)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(invalidUserWithMissingSymbol)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get(USERS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // Проверяет попытку добавления нового пользователя с некорректной датой рождения
    @Test
    void shouldNotAddUserWithInvalidBirthday() throws Exception {
        final User guestFromTheFuture = User.builder()
                .name("unknown")
                .login("unknown")
                .email("unknown@test.com")
                .birthday(LocalDate.of(2099, 1, 1))
                .build();

        mockMvc.perform(post(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(guestFromTheFuture)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get(USERS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // Проверяет попытку добавления пользователя с email, который уже используется
    @Test
    void shouldNotAddUserWithEmailAlreadyInUse() throws Exception {
        fillWithValidData();

        final User user = User.builder()
                .name("lesha")
                .login("lesha")
                .email("user1@test.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        mockMvc.perform(post(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(user)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get(USERS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    // Проверяет попытку отправки POST-запроса с пустым body
    @Test
    void shouldHandleEmptyRequest() throws Exception {
        // TODO: можно обрабатывать HttpMessageNotReadableException и возвращать 400
        mockMvc.perform(post(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isInternalServerError());
    }

    // Проверяет получение всех пользователей
    @Test
    void shouldGetAllUsers() throws Exception {
        fillWithValidData();

        mockMvc.perform(get(USERS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    // Проверяет обновление существующего пользователя (всех полей)
    @Test
    void shouldUpdateUserAllFields() throws Exception {
        fillWithValidData();

        final User user = User.builder()
                .id(1)
                .name("katya")
                .login("katya")
                .email("katya@test.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        MvcResult result = mockMvc.perform(put(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(user)))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        User responseUser = gson.fromJson(json, User.class);
        assertEquals(user.getName(), responseUser.getName(), "Не совпадают имена");
        assertEquals(user.getLogin(), responseUser.getLogin(), "Не совпадают логины");
        assertEquals(user.getEmail(), responseUser.getEmail(), "Не совпадают email");
        assertEquals(user.getBirthday(), responseUser.getBirthday(), "Не совпадают даты рождения");
    }

    // Проверяет обновление существующего пользователя (всех полей, кроме email)
    @Test
    void shouldUpdateUserAllFieldsExceptEmail() throws Exception {
        fillWithValidData();

        final User user = User.builder()
                .id(1)
                .name("katya")
                .login("katya")
                .email("user1@test.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        MvcResult result = mockMvc.perform(put(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(user)))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        User responseUser = gson.fromJson(json, User.class);
        assertEquals(user.getName(), responseUser.getName(), "Не совпадают имена");
        assertEquals(user.getLogin(), responseUser.getLogin(), "Не совпадают логины");
        assertEquals(user.getEmail(), responseUser.getEmail(), "Не совпадают email");
        assertEquals(user.getBirthday(), responseUser.getBirthday(), "Не совпадают даты рождения");
    }

    // Проверяет обновление несуществующего пользователя
    @Test
    void shouldNotUpdateUnknownUser() throws Exception {
        fillWithValidData();

        final User user = User.builder()
                .id(100)
                .name("sasha")
                .login("sasha")
                .email("sasha@test.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        mockMvc.perform(put(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(user)))
                .andExpect(status().isNotFound());
    }

    // Проверяет попытку изменения email на другой, который уже используется
    @Test
    void shouldNotUpdateEmailIfAlreadyInUse() throws Exception {
        fillWithValidData();

        final User user = User.builder()
                .id(1)
                .name("zhenya")
                .login("zhenya")
                .email("user2@test.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        mockMvc.perform(put(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(user)))
                .andExpect(status().isBadRequest());
    }

    // Проверяет получение пользователя по id
    @Test
    void shouldReturnUserById() throws Exception {
        fillWithValidData();

        mockMvc.perform(get(USERS_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    // Проверяет получение несуществующего пользователя
    @Test
    void shouldReturnNotFoundIfUserWithSpecifiedIdDoesntExist() throws Exception {
        fillWithValidData();

        mockMvc.perform(get(USERS_URL + "/4"))
                .andExpect(status().isNotFound());
    }

    // Проверяет добавление двух пользователей в друзья
    @Test
    void shouldAddToFriends() throws Exception {
        fillWithValidData();

        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isOk());
    }

    // Проверяет попытку добавления в друзья пользователя, которого не существует
    @Test
    void shouldReturnNotFoundIfOneOfUsersDoesntExist() throws Exception {
        fillWithValidData();

        mockMvc.perform(put("/users/1/friends/4"))
                .andExpect(status().isNotFound());
        mockMvc.perform(put("/users/4/friends/1"))
                .andExpect(status().isNotFound());
    }

    // Проверяет повторную попытку добавления в друзья
    @Test
    void shouldReturnBadRequestIfUsersAreAlreadyFriends() throws Exception {
        fillWithValidData();

        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isBadRequest());
    }

    // Проверяет удаление из друзей
    @Test
    void shouldRemoveFromFriends() throws Exception {
        fillWithValidData();

        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/users/1/friends/2"))
                .andExpect(status().isOk());
    }

    // Проверяет возвращение списка друзей
    @Test
    void shouldGetAllFriends() throws Exception {
        fillWithValidData();

        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/users/1/friends/3"))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andReturn();

        TypeToken<List<User>> typeToken = new TypeToken<>(){};
        String json = result.getResponse().getContentAsString();
        List<User> users = gson.fromJson(json, typeToken.getType());
        assertTrue(users.stream()
                .anyMatch(item -> item.getId().equals(2) || item.getId().equals(3)));
    }

    // Проверяет возвращение списка общих друзей
    @Test
    void shouldGetCommonFriends() throws Exception {
        fillWithValidData();

        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/users/3/friends/2"))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/users/1/friends/common/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andReturn();

        TypeToken<List<User>> typeToken = new TypeToken<>(){};
        String json = result.getResponse().getContentAsString();
        List<User> users = gson.fromJson(json, typeToken.getType());
        assertTrue(users.stream().anyMatch(item -> item.getId().equals(2)));
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
            mockMvc.perform(post(USERS_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(user)))
                    .andExpect(status().isCreated());
        }
    }
}
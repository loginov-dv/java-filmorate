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
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;

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
@AutoConfigureTestDatabase
@Sql(scripts = { "/schema.sql", "/data.sql", "/test-data.sql" })
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    // Gson
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();
    // Путь
    private static final String USERS_URL = "/users";

    // Проверяет добавление нового пользователя
    @Test
    @Order(7)
    void shouldAddNewValidUser() throws Exception {
        NewUserRequest validUser = new NewUserRequest();
        validUser.setName("vasya");
        validUser.setLogin("vasya");
        validUser.setEmail("vasya@test.com");
        validUser.setBirthday(LocalDate.of(2000, 1, 1));

        MvcResult response = mockMvc.perform(post(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(validUser)))
                .andExpect(status().isCreated())
                .andReturn();

        mockMvc.perform(get(USERS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6));

        String json = response.getResponse().getContentAsString();
        UserDto userDto = gson.fromJson(json, UserDto.class);
        assertEquals(6, userDto.getId(), "Не совпадают id");
        assertEquals(validUser.getName(), userDto.getName(), "Не совпадают имена");
        assertEquals(validUser.getLogin(), userDto.getLogin(), "Не совпадают логины");
        assertEquals(validUser.getEmail(), userDto.getEmail(), "Не совпадают email");
        assertEquals(validUser.getBirthday(), userDto.getBirthday(), "Не совпадают даты рождения");
    }

    // Проверяет попытку добавления нового пользователя с некорректным логином
    @Test
    @Order(2)
    void shouldNotAddUserWithInvalidLogin() throws Exception {
        NewUserRequest invalidUserWithNullLogin = new NewUserRequest();
        invalidUserWithNullLogin.setName("petya");
        invalidUserWithNullLogin.setEmail("petya@test.com");
        invalidUserWithNullLogin.setBirthday(LocalDate.of(2000, 1, 1));

        NewUserRequest ivalidUserWithEmptyLogin = new NewUserRequest();
        invalidUserWithNullLogin.setName("sasha");
        invalidUserWithNullLogin.setLogin("");
        invalidUserWithNullLogin.setEmail("sasha@test.com");
        invalidUserWithNullLogin.setBirthday(LocalDate.of(2000, 1, 1));

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
                .andExpect(jsonPath("$.length()").value(5));
    }

    // Проверяет попытку добавления нового пользователя с логином, содержащим пробелы
    @Test
    @Order(3)
    void shouldNotAddUserWithWhitespaceInLogin() throws Exception {
        NewUserRequest invalidUser1 = new NewUserRequest();
        invalidUser1.setName("masha");
        invalidUser1.setLogin("ma sha");
        invalidUser1.setEmail("masha@test.com");
        invalidUser1.setBirthday(LocalDate.of(2000, 1, 1));

        NewUserRequest invalidUser2 = new NewUserRequest();
        invalidUser2.setName("masha");
        invalidUser2.setLogin(" masha");
        invalidUser2.setEmail("masha@test.com");
        invalidUser2.setBirthday(LocalDate.of(2000, 1, 1));

        NewUserRequest invalidUser3 = new NewUserRequest();
        invalidUser3.setName("masha");
        invalidUser3.setLogin("masha ");
        invalidUser3.setEmail("masha@test.com");
        invalidUser3.setBirthday(LocalDate.of(2000, 1, 1));

        NewUserRequest invalidUser4 = new NewUserRequest();
        invalidUser4.setName("masha");
        invalidUser4.setLogin("mas     ha");
        invalidUser4.setEmail("masha@test.com");
        invalidUser4.setBirthday(LocalDate.of(2000, 1, 1));

        NewUserRequest invalidUser5 = new NewUserRequest();
        invalidUser5.setName("masha");
        invalidUser5.setLogin(" mas ha ");
        invalidUser5.setEmail("masha@test.com");
        invalidUser5.setBirthday(LocalDate.of(2000, 1, 1));

        List<NewUserRequest> invalidUsers = List.of(invalidUser1, invalidUser2, invalidUser3, invalidUser4, invalidUser5);

        for (NewUserRequest invalidUser : invalidUsers) {
            mockMvc.perform(post(USERS_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(invalidUser)))
                    .andExpect(status().isBadRequest());
        }

        mockMvc.perform(get(USERS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));
    }

    // Проверяет попытку добавления нового пользователя с некорректной электронной почтой
    @Test
    @Order(4)
    void shouldNotAddUserWithInvalidEmail() throws Exception {
        NewUserRequest invalidUserWithNullEmail = new NewUserRequest();
        invalidUserWithNullEmail.setName("kolya");
        invalidUserWithNullEmail.setLogin("kolya");
        invalidUserWithNullEmail.setBirthday(LocalDate.of(2000, 1, 1));

        NewUserRequest ivalidUserWithEmptyEmail = new NewUserRequest();
        invalidUserWithNullEmail.setName("nastya");
        invalidUserWithNullEmail.setLogin("nastya");
        ivalidUserWithEmptyEmail.setEmail("");
        invalidUserWithNullEmail.setBirthday(LocalDate.of(2000, 1, 1));

        NewUserRequest invalidUserWithMissingSymbol = new NewUserRequest();
        invalidUserWithMissingSymbol.setName("olya");
        invalidUserWithMissingSymbol.setLogin("olya");
        invalidUserWithMissingSymbol.setEmail("olya");
        invalidUserWithMissingSymbol.setBirthday(LocalDate.of(2000, 1, 1));

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
                .andExpect(jsonPath("$.length()").value(5));
    }

    // Проверяет попытку добавления нового пользователя с некорректной датой рождения
    @Test
    @Order(5)
    void shouldNotAddUserWithInvalidBirthday() throws Exception {
        NewUserRequest guestFromTheFuture = new NewUserRequest();
        guestFromTheFuture.setName("unknown");
        guestFromTheFuture.setLogin("unknown");
        guestFromTheFuture.setEmail("unknown@test.com");
        guestFromTheFuture.setBirthday(LocalDate.of(2099, 1, 1));

        mockMvc.perform(post(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(guestFromTheFuture)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get(USERS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));
    }

    // Проверяет попытку добавления пользователя с email, который уже используется
    @Test
    @Order(6)
    void shouldNotAddUserWithEmailAlreadyInUse() throws Exception {
        NewUserRequest user = new NewUserRequest();
        user.setName("lesha");
        user.setLogin("lesha");
        user.setEmail("user1@example.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        mockMvc.perform(post(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(user)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get(USERS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));
    }

    // Проверяет попытку отправки POST-запроса с пустым body
    @Test
    void shouldHandleEmptyRequest() throws Exception {
        mockMvc.perform(post(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isInternalServerError());
    }

    // Проверяет получение всех пользователей
    @Test
    @Order(1)
    void shouldGetAllUsers() throws Exception {
        mockMvc.perform(get(USERS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));
    }

    // Проверяет обновление существующего пользователя (всех полей)
    @Test
    void shouldUpdateUserAllFields() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setId(1);
        request.setName("katya");
        request.setLogin("katya");
        request.setEmail("katya@test.com");
        request.setBirthday(LocalDate.of(2000, 1, 10));

        MvcResult result = mockMvc.perform(put(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(request)))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        UserDto userDto = gson.fromJson(json, UserDto.class);
        assertEquals(1, userDto.getId(), "Не совпадают id");
        assertEquals(request.getName(), userDto.getName(), "Не совпадают имена");
        assertEquals(request.getLogin(), userDto.getLogin(), "Не совпадают логины");
        assertEquals(request.getEmail(), userDto.getEmail(), "Не совпадают email");
        assertEquals(request.getBirthday(), userDto.getBirthday(), "Не совпадают даты рождения");
    }

    // Проверяет обновление несуществующего пользователя
    @Test
    void shouldNotUpdateUnknownUser() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setId(100);
        request.setName("sasha");
        request.setLogin("sasha");
        request.setEmail("sasha@test.com");
        request.setBirthday(LocalDate.of(2000, 1, 10));

        mockMvc.perform(put(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(request)))
                .andExpect(status().isNotFound());
    }

    // Проверяет попытку изменения email на другой, который уже используется
    // Проверяет попытку изменения email на другой, который уже используется
    @Test
    void shouldNotUpdateEmailIfAlreadyInUse() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setId(3);
        request.setName("zhenya");
        request.setLogin("zhenya");
        request.setEmail("user2@example.com");
        request.setBirthday(LocalDate.of(2000, 1, 10));

        mockMvc.perform(put(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(request)))
                .andExpect(status().isBadRequest());
    }

    // Проверяет получение пользователя по id
    @Test
    void shouldGetUserById() throws Exception {
        mockMvc.perform(get(USERS_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    // Проверяет получение несуществующего пользователя
    @Test
    void shouldReturnNotFoundIfUserWithSpecifiedIdDoesntExist() throws Exception {
        mockMvc.perform(get(USERS_URL + "/100"))
                .andExpect(status().isNotFound());
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
        mockMvc.perform(put("/users/1/friends/100"))
                .andExpect(status().isNotFound());
        mockMvc.perform(put("/users/100/friends/1"))
                .andExpect(status().isNotFound());
    }

    // Проверяет повторную попытку добавления в друзья
    @Test
    void shouldReturnBadRequestIfUsersAreAlreadyFriends() throws Exception {
        mockMvc.perform(put("/users/1/friends/3"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/users/1/friends/3"))
                .andExpect(status().isBadRequest());
    }

    // Проверяет удаление из друзей
    @Test
    void shouldRemoveFromFriends() throws Exception {
        mockMvc.perform(put("/users/1/friends/4"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/users/1/friends/4"))
                .andExpect(status().isNoContent());
    }

    // Проверяет возвращение списка друзей
    @Test
    void shouldGetAllFriends() throws Exception {
        mockMvc.perform(put("/users/2/friends/4"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/users/2/friends/5"))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/users/2/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andReturn();

        TypeToken<List<UserDto>> typeToken = new TypeToken<>(){};
        String json = result.getResponse().getContentAsString();
        List<UserDto> users = gson.fromJson(json, typeToken.getType());
        assertTrue(users.stream()
                .anyMatch(item -> item.getId() == 4 || item.getId() == 5));
    }

    // Проверяет возвращение списка общих друзей
    @Test
    void shouldGetCommonFriends() throws Exception {
        mockMvc.perform(put("/users/3/friends/5"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/users/4/friends/5"))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/users/3/friends/common/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andReturn();

        TypeToken<List<UserDto>> typeToken = new TypeToken<>(){};
        String json = result.getResponse().getContentAsString();
        List<UserDto> users = gson.fromJson(json, typeToken.getType());
        assertTrue(users.stream().anyMatch(item -> item.getId() == 5));
    }
}
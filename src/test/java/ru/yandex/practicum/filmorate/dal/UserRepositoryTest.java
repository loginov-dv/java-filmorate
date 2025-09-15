package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@Sql(scripts = {"/schema.sql", "/data.sql", "/test-data.sql"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {UserRowMapper.class, UserRepository.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserRepositoryTest {
    private final UserRepository userRepository;

    @Test
    void shouldFindUserById() {
        Optional<User> maybeUser = userRepository.getById(1);

        assertThat(maybeUser)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("login", "user1"));
    }

    @Test
    @Order(1)
    void shouldFindAllUsers() {
        List<User> users = userRepository.getAll();

        assertEquals(5, users.size());
    }

    @Test
    void shouldFindUserByEmail() {
        Optional<User> maybeUser = userRepository.getByEmail("user3@example.com");

        assertThat(maybeUser)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 3))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("login", "user3"))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", "user3@example.com"));
    }

    @Test
    void shouldCreateUser() {
        User newUser = new User();
        newUser.setEmail("new@example.com");
        newUser.setLogin("new");
        newUser.setName("new");
        newUser.setBirthday(LocalDate.of(2000, 10, 10));

        User newUserFromDB = userRepository.create(newUser);

        Optional<User> maybeUser = userRepository.getById(newUserFromDB.getId());
        assertThat(maybeUser)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 6))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("login", newUser.getLogin()))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", newUser.getEmail()))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("name", newUser.getName()))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("birthday", newUser.getBirthday()));
    }

    @Test
    void shouldUpdateUser() {
        User updatedUser = new User();
        updatedUser.setId(2);
        updatedUser.setEmail("update@example.com");
        updatedUser.setLogin("update");
        updatedUser.setName("update");
        updatedUser.setBirthday(LocalDate.of(1990, 5, 5));

        userRepository.update(updatedUser);

        Optional<User> maybeUser = userRepository.getById(updatedUser.getId());
        assertThat(maybeUser)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 2))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("login", updatedUser.getLogin()))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", updatedUser.getEmail()))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("name", updatedUser.getName()))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("birthday", updatedUser.getBirthday()));
    }

    @Test
    void shouldAddFriend() {
        List<User> friends = userRepository.getFriends(1);
        assertEquals(0, friends.size());

        userRepository.addFriend(1, 2);
        friends = userRepository.getFriends(1);

        assertEquals(1, friends.size());
        assertEquals(2, friends.getFirst().getId());
    }

    @Test
    void shouldRemoveUser() {
        List<User> friends = userRepository.getFriends(1);
        assertEquals(0, friends.size());

        userRepository.addFriend(1, 2);
        friends = userRepository.getFriends(1);

        assertEquals(1, friends.size());
        assertEquals(2, friends.getFirst().getId());

        userRepository.removeFriend(1, 2);
        friends = userRepository.getFriends(1);

        assertEquals(0, friends.size());
    }

    @Test
    void shouldReturnFriends() {
        List<User> friends = userRepository.getFriends(1);
        assertEquals(0, friends.size());

        userRepository.addFriend(1, 2);
        userRepository.addFriend(1, 3);

        friends = userRepository.getFriends(1);

        assertEquals(2, friends.size());
    }

    @Test
    void shouldRemoveUserById() {
        User newUser = new User();
        newUser.setEmail("new@example.com");
        newUser.setLogin("new");
        newUser.setName("new");
        newUser.setBirthday(LocalDate.of(2000, 10, 10));

        userRepository.create(newUser);
        userRepository.removeUserById(1);
        assertThat(userRepository.getById(1)).isEmpty();
    }
}
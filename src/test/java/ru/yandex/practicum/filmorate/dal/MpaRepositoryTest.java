package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {MpaRowMapper.class, MpaRepository.class})
class MpaRepositoryTest {
    private final MpaRepository mpaRepository;

    @Test
    void shouldFindMpaById() {
        Optional<MpaRating> maybeRating = mpaRepository.getById(1);

        assertThat(maybeRating)
                .isPresent()
                .hasValueSatisfying(mpaRating ->
                        assertThat(mpaRating).hasFieldOrPropertyWithValue("id", 1))
                .hasValueSatisfying(mpaRating ->
                        assertThat(mpaRating).hasFieldOrPropertyWithValue("name", "G"));
    }

    @Test
    void shouldFindAllMpa() {
        List<MpaRating> mpaRatingList = mpaRepository.getAll();

        assertEquals(5, mpaRatingList.size());
    }
}
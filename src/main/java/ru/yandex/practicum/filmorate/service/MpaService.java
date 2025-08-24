package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;
import java.util.Optional;

// Сервис по работе с MPA
@Service
public class MpaService {
    // Репозиторий MPA-рейтингов
    private final MpaRepository mpaRepository;
    // Логгер
    private static final Logger logger = LoggerFactory.getLogger(MpaService.class);

    @Autowired
    public MpaService(MpaRepository mpaRepository) {
        this.mpaRepository = mpaRepository;
    }

    // Вернуть все рейтинги
    public List<MpaRating> getAll() {
        logger.debug("Запрос на получение всех рейтингов");
        return mpaRepository.getAll();
    }

    // Вернуть рейтинг по id
    public MpaRating getById(int id) {
        logger.debug("Запрос на получение рейтинга с id = {}", id);

        Optional<MpaRating> maybeMpa = mpaRepository.getById(id);

        if (maybeMpa.isEmpty()) {
            logger.warn("Рейтинг с id = {} не найден", id);
            throw new NotFoundException("Рейтинг с id = " + id + " не найден");
        }

        return maybeMpa.get();
    }
}

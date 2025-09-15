package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.DirectorRepository;
import ru.yandex.practicum.filmorate.dto.NewDirectorRequest;
import ru.yandex.practicum.filmorate.dto.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

@Service
public class DirectorService {
    private final DirectorRepository directorRepository;
    private static final Logger logger = LoggerFactory.getLogger(DirectorService.class);

    @Autowired
    public DirectorService(DirectorRepository directorRepository) {
        this.directorRepository = directorRepository;
    }

    public List<Director> getAll() {
        logger.debug("Запрос на получение всех режиссёров");
        return directorRepository.getAll();
    }

    public Director getById(int id) {
        logger.debug("Запрос на получение режиссёра с id = {}", id);

        Optional<Director> maybeDirector = directorRepository.getById(id);

        if (maybeDirector.isEmpty()) {
            logger.warn("Режиссёр с id = {} не найден", id);
            throw new NotFoundException("Режиссёр с id = " + id + " не найден");
        }

        return maybeDirector.get();
    }

    public Director create(NewDirectorRequest request) {
        logger.debug("Запрос на создания нового режиссёра");
        logger.debug("Входные данные: {}", request);

        Director director = DirectorMapper.mapToDirector(request);
        director = directorRepository.create(director);

        logger.info("Создан режиссёр: {}", director);
        return director;
    }

    public Director update(UpdateDirectorRequest request) {
        logger.debug("Запрос на изменение режиссёра с id = {}", request.getId());
        logger.debug("Входные данные: {}", request);

        if (request.getId() == null) {
            logger.warn("Не указан id");
            throw new NotFoundException("Не указан id");
        }

        Optional<Director> maybeDirector = directorRepository.getById(request.getId());

        if (maybeDirector.isEmpty()) {
            logger.warn("Режиссёр с id = {} не найден", request.getId());
            throw new NotFoundException("Режиссёр с id = " + request.getId() + " не найден");
        }

        Director director = maybeDirector.get();
        logger.debug("Исходное состояние: {}", director);

        Director updatedDirector = DirectorMapper.updateDirectorFields(director, request);
        updatedDirector = directorRepository.update(updatedDirector);

        logger.info("Изменен режиссёр: {}", updatedDirector);
        return updatedDirector;
    }

    public void removeById(int id) {
        logger.debug("Запрос на изменение режиссёра с id = {}", id);

        Optional<Director> maybeDirector = directorRepository.getById(id);

        if (maybeDirector.isEmpty()) {
            logger.warn("Режиссёр с id = {} не найден", id);
            throw new NotFoundException("Режиссёр с id = " + id + " не найден");
        }

        directorRepository.removeById(id);
        logger.debug("Удалён режиссёр с id = {}", id);
    }
}

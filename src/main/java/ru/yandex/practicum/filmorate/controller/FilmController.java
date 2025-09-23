package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validation.CreateValidation;
import ru.yandex.practicum.filmorate.validation.FilmValidator;
import ru.yandex.practicum.filmorate.validation.UpdateValidation;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
@Validated
public class FilmController {
    private final FilmService filmService;
    private final FilmValidator filmValidator;

    @Autowired
    public FilmController(FilmService filmService, FilmValidator filmValidator) {
        this.filmService = filmService;
        this.filmValidator = filmValidator;
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Получен запрос на получение всех фильмов");
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable Long id) {
        log.info("Получен запрос на получение фильма с id = {}", id);
        return filmService.findById(id);
    }

    @PostMapping
    @Validated(CreateValidation.class)
    public Film create(@Valid @RequestBody Film film) {
        log.info("Получен запрос на добавление фильма: {}", film);
        filmValidator.validateForCreate(film);
        Film createdFilm = filmService.create(film);
        log.info("Фильм добавлен успешно: {}", createdFilm);
        return createdFilm;
    }

    @PutMapping
    @Validated(UpdateValidation.class)
    public Film update(@Valid @RequestBody Film film) {
        log.info("Получен запрос на обновление фильма: {}", film);

        if (film.getId() == null) {
            throw new ValidationException("ID фильма не может быть null");
        }

        filmService.findById(film.getId());

        filmValidator.validateForUpdate(film);
        Film updatedFilm = filmService.update(film);
        log.info("Фильм обновлен успешно: {}", updatedFilm);
        return updatedFilm;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен запрос на добавление лайка фильму {} от пользователя {}", id, userId);
        filmService.addLike(id, userId);
        log.info("Лайк добавлен успешно");
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.removeLike(id, userId);
        log.info("Лайк удален успешно");
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Получен запрос на получение {} популярных фильмов", count);
        return filmService.getPopularFilms(count);
    }
}

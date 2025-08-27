package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.CreateValidation;
import ru.yandex.practicum.filmorate.validation.UpdateValidation;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
@Validated
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Получен запрос на получение всех фильмов. Текущее количество: {}", films.size());
        return films.values();
    }

    @PostMapping
    @Validated(CreateValidation.class)
    public Film create(@Valid @RequestBody Film film) {
        log.info("Получен запрос на добавление фильма: {}", film);
        validateReleaseDate(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм добавлен успешно: {}", film);
        return film;
    }

    @PutMapping
    @Validated(UpdateValidation.class)
    public Film update(@Valid @RequestBody Film film) {
        log.info("Получен запрос на обновление фильма: {}", film);

        if (film.getId() == null) {
            log.error("Id фильма должен быть указан");
            throw new ValidationException("Id фильма должен быть указан");
        }

        if (!films.containsKey(film.getId())) {
            log.error("Фильм с id = {} не найден", film.getId());
            throw new ValidationException("Фильм с id = " + film.getId() + " не найден");
        }

        validateReleaseDate(film);
        films.put(film.getId(), film);
        log.info("Фильм обновлен успешно: {}", film);
        return film;
    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.error("Дата релиза фильма не может быть раньше {}", MIN_RELEASE_DATE);
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }

    private Long getNextId() {
        return films.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0) + 1;
    }
}

package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

@GetMapping
    public Collection<Film> findAll() {
       log.info("Получен запрос на получение всех фильмов. Текущее количество: {}", films.size());
       return films.values();
}

@PostMapping
    public Film create(@RequestBody Film film) {
       log.info("Получен запрос на добавление фильма: {}", film);
       validateFilm(film);
       film.setId(getNextId());
       films.put(film.getId(), film);
       log.info("Фильм добавлен успешно: {}", film);
       return film;
}

@PutMapping
public Film update(@RequestBody Film film) {
    log.info("Получен запрос на обновление фильма: {}", film);

    if (film.getId() == null) {
        log.error("Id фильма должен быть указан");
        throw new ValidationException("Id фильма должен быть указан");
    }

    if (!films.containsKey(film.getId())) {
        log.error("Фильм с id = {} не найден", film.getId());
        throw new ValidationException("Фильм с id = " + film.getId() + " не найден");
    }

    validateFilm(film);
    films.put(film.getId(), film);
    log.info("Фильм обновлен успешно: {}", film);
    return film;
}

private void validateFilm(Film film) {
    if (film.getName() == null || film.getName().isBlank()) {
        log.error("Название фильма не может быть пустым");
        throw new ValidationException("Название не может быть пустым");
    }
    if (film.getDescription() != null && film.getDescription().length() > 200) {
        log.error("Описание фильма превышает 200 символов");
        throw new ValidationException("Максимальная длина описания — 200 символов");
    }
    if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
        log.error("Дата релиза фильма не может быть раньше {}", MIN_RELEASE_DATE);
        throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
    }
    if (film.getDuration() == null || film.getDuration() <= 0) {
        log.error("Продолжительность фильма должна быть положительным числом");
        throw new ValidationException("Продолжительность должна быть положительным числом");
    }
}

    private Long getNextId() {
        return films.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0) + 1;
    }
}

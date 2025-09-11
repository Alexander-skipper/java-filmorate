package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film findById(Long id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new FilmNotFoundException("Фильм с id = " + id + " не найден"));
    }

    public void addLike(Long filmId, Long userId) {
        Film film = findById(filmId);
        User user = userStorage.findById(userId)
                .orElseThrow(() ->
                    new UserNotFoundException("Пользователь с id = " + userId + " не найден"));

        boolean added = film.addLike(userId);

        if (!added) {
            log.trace("Пользователь {} уже лайкал фильм {}", userId, filmId);
            return;
        }
        filmStorage.update(film);
        log.trace("Лайк добавлен: фильм {}, пользователь {}", filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = findById(filmId);
        User user = userStorage.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("Пользователь с id = " + userId + " не найден"));

        boolean removed = film.removeLike(userId);
        if (!removed) {
            log.trace("Пользователь {} не лайкал фильм {}", userId, filmId);
            return;
        }
        filmStorage.update(film);
        log.trace("Лайк удален: фильм {}, пользователь {}", filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt(Film::getLikesCount).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}

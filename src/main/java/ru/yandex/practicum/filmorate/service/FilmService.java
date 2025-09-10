package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final Map<Long, Set<Long>> filmLikes = new HashMap<>();

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
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
        userService.findById(userId);

        Set<Long> userLikes = filmLikes.getOrDefault(filmId, new HashSet<>());

        if (userLikes.contains(userId)) {
            throw new ValidationException("Пользователь уже поставил лайк этому фильму");
        }
        userLikes.add(userId);
        filmLikes.put(filmId, userLikes);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = findById(filmId);
        userService.findById(userId);

        Set<Long> userLikes = filmLikes.getOrDefault(filmId, Collections.emptySet());
        if (!userLikes.contains(userId)) {
            throw new ValidationException("Лайк не найден");
        }
        userLikes.remove(userId);
        filmLikes.put(filmId, userLikes);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt(film -> -getLikesCount(film.getId())))
                .limit(count)
                .collect(Collectors.toList());
    }

    private int getLikesCount(Long filmId) {
        return filmLikes.getOrDefault(filmId, Collections.emptySet()).size();
    }
}

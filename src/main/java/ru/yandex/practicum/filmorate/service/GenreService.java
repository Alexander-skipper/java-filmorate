package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.GenreDbStorage;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreDbStorage genreStorage;

    public List<Genre> getAllGenres() {
        return genreStorage.findAll();
    }

    public Genre getGenreById(Long id) {
        return genreStorage.findById(id)
                .orElseThrow(() -> new FilmNotFoundException("Жанр с id = " + id + " не найден"));
    }
}

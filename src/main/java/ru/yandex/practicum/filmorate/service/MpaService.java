package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.MpaDbStorage;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaDbStorage mpaStorage;

    public List<Mpa> getAllMpa() {
        return mpaStorage.findAll();
    }

    public Mpa getMpaById(Long id) {
        return mpaStorage.findById(id)
                .orElseThrow(() -> new FilmNotFoundException("Рейтинг MPA с id = " + id + " не найден"));
    }
}

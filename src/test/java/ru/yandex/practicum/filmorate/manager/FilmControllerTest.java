package ru.yandex.practicum.filmorate.manager;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FilmControllerTest {
    private FilmController filmController;
    private Film validFilm;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        validFilm = new Film();
        validFilm.setName("Valid Film");
        validFilm.setDescription("Valid description");
        validFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        validFilm.setDuration(120);
    }

    @Test
    void createFilm_WithValidData_ShouldSuccess() {
        Film createdFilm = filmController.create(validFilm);

        assertNotNull(createdFilm.getId());
        assertEquals("Valid Film", createdFilm.getName());
        assertEquals(120, createdFilm.getDuration());
    }

    @Test
    void createFilm_WithEarlyReleaseDate_ShouldThrowValidationException() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void createFilm_WithMinReleaseDate_ShouldSuccess() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(120);

        Film createdFilm = filmController.create(film);
        assertNotNull(createdFilm);
    }

    @Test
    void createFilm_WithNullReleaseDate_ShouldThrowValidationException() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(null);
        film.setDuration(120);

        assertThrows(Exception.class, () -> filmController.create(film));
    }

    @Test
    void createFilm_WithReleaseDateBeforeMin_ShouldThrowValidationException() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 27)); // На день раньше минимума
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }


    @Test
    void updateFilm_WithNonExistentId_ShouldThrowValidationException() {
        Film film = new Film();
        film.setId(999L);
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> filmController.update(film));
    }

    @Test
    void updateFilm_WithNullId_ShouldThrowValidationException() {
        Film film = new Film();
        film.setId(null);
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> filmController.update(film));
    }
}

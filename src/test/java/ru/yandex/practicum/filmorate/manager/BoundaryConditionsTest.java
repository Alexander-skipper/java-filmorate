package ru.yandex.practicum.filmorate.manager;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BoundaryConditionsTest {
    private final FilmController filmController = new FilmController();
    private final UserController userController = new UserController();

    @Test
    void filmDescription_Exactly200Characters_ShouldSuccess() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("A".repeat(200));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Film createdFilm = filmController.create(film);
        assertNotNull(createdFilm);
    }

    @Test
    void filmDescription_201Characters_ShouldThrowValidationException() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("A".repeat(201)); // 201 символ
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void userEmail_WithValidEmail_ShouldSuccess() {
        User user = new User();

        user.setEmail("test@domain.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userController.create(user);
        assertNotNull(createdUser);
        assertEquals("test@domain.com", createdUser.getEmail());
    }


    @Test
    void userBirthday_ExactlyToday_ShouldSuccess() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.now());

        User createdUser = userController.create(user);
        assertNotNull(createdUser);
    }

    @Test
    void filmReleaseDate_ExactlyMinDate_ShouldSuccess() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(120);

        Film createdFilm = filmController.create(film);
        assertNotNull(createdFilm);
    }

    @Test
    void filmDuration_MinPositiveValue_ShouldSuccess() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(1);

        Film createdFilm = filmController.create(film);
        assertNotNull(createdFilm);
    }
}

package ru.yandex.practicum.filmorate.manager.dla;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.FilmDbStorage;
import ru.yandex.practicum.filmorate.dal.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRowMapper.class})
public class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;

    @Test
    void testFindFilmById() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpaId(1L);

        Film createdFilm = filmStorage.create(film);
        Optional<Film> filmOptional = filmStorage.findById(createdFilm.getId());

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("filmId", createdFilm.getId())
                );
    }
}

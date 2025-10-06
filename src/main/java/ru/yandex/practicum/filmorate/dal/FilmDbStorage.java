package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;

@Repository
@Qualifier("filmDbStorage")
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Film> findAll() {
        String sql = "SELECT * FROM films ORDER BY film_id ASC";
        List<Film> films = findMany(sql);
        films.forEach(this::loadAdditionalData);
        return films;
    }

    @Override
    public Film create(Film film) {
        // Преобразуем genres в genreIds для БД
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            film.getGenreIds().clear();
            for (Genre genre : film.getGenres()) {
                if (genre.getId() != null) {
                    film.getGenreIds().add(genre.getId());
                }
            }
        }

        // Сохраняем mpaId из mpa объекта
        if (film.getMpa() != null && film.getMpa().getId() != null) {
            film.setMpaId(film.getMpa().getId());
        }

        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        try {
            long id = insert(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpaId());
            film.setId(id);

            // Обновляем связи используя genreIds
            updateGenres(film);
            updateLikes(film);

            return findById(id).orElse(film);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании фильма", e);
        }
    }

    @Override
    public Film update(Film film) {
        findById(film.getId()).orElseThrow(() ->
                new FilmNotFoundException("Фильм с id = " + film.getId() + " не найден"));

        // Преобразуем genres в genreIds для БД
        if (film.getGenres() != null) {
            film.getGenreIds().clear();
            for (Genre genre : film.getGenres()) {
                if (genre.getId() != null) {
                    film.getGenreIds().add(genre.getId());
                }
            }
        }

        // Сохраняем mpaId из mpa объекта
        if (film.getMpa() != null && film.getMpa().getId() != null) {
            film.setMpaId(film.getMpa().getId());
        }

        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";
        try {
            update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpaId(), film.getId());

            updateGenres(film);
            updateLikes(film);

            return findById(film.getId()).orElse(film);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при обновлении фильма", e);
        }
    }

    @Override
    public Optional<Film> findById(Long id) {
        String sql = "SELECT * FROM films WHERE film_id = ?";
        Optional<Film> film = findOne(sql, id);
        film.ifPresent(this::loadAdditionalData);
        return film;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM films WHERE film_id = ?";
        jdbc.update(sql, id);
    }

    @Override
    public void deleteAll() {
        jdbc.update("DELETE FROM film_genres");
        jdbc.update("DELETE FROM film_likes");
        jdbc.update("DELETE FROM films");
        jdbc.update("ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1");
    }

    public void addLike(Long filmId, Long userId) {
        jdbc.update("INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)", filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        jdbc.update("DELETE FROM film_likes WHERE film_id = ? AND user_id = ?", filmId, userId);
    }

    public boolean hasLike(Long filmId, Long userId) {
        String sql = "SELECT COUNT(*) FROM film_likes WHERE film_id = ? AND user_id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, filmId, userId);
        return count != null && count > 0;
    }

    private void loadAdditionalData(Film film) {
        if (film == null || film.getId() == null) return;
        Long originalId = film.getId();
        loadGenreIds(film);      // Загружаем ID в genreIds
        loadGenreObjects(film);  // Загружаем объекты в genres
        loadLikes(film);
        loadMpa(film);
        if (!originalId.equals(film.getId())) {
            film.setId(originalId);
        }
    }

    private void loadGenreIds(Film film) {
        String sql = "SELECT genre_id FROM film_genres WHERE film_id = ? ORDER BY genre_id";
        List<Long> genreIds = jdbc.queryForList(sql, Long.class, film.getId());
        film.getGenreIds().clear();
        film.getGenreIds().addAll(genreIds);
    }

    private void loadGenreObjects(Film film) {
        if (film.getGenreIds().isEmpty()) {
            film.setGenres(new ArrayList<>());
            return;
        }

        String placeholders = String.join(",", Collections.nCopies(film.getGenreIds().size(), "?"));
        String sql = "SELECT genre_id, name FROM genres WHERE genre_id IN (" + placeholders + ") ORDER BY genre_id";

        List<Genre> genres = jdbc.query(sql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getLong("genre_id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, film.getGenreIds().toArray());

        film.setGenres(genres);
    }

    private void loadLikes(Film film) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";
        List<Long> likeIds = jdbc.queryForList(sql, Long.class, film.getId());
        film.getLikes().clear();
        film.getLikes().addAll(likeIds);
    }

    private void loadMpa(Film film) {
        if (film == null || film.getId() == null) return;
        String sql = "SELECT m.mpa_id, m.name, m.description FROM mpa_ratings m JOIN films f ON m.mpa_id = f.mpa_id WHERE f.film_id = ?";
        try {
            Mpa mpa = jdbc.queryForObject(sql, (rs, rowNum) -> {
                Mpa result = new Mpa();
                result.setId(rs.getLong("mpa_id"));
                result.setName(rs.getString("name"));
                result.setDescription(rs.getString("description"));
                return result;
            }, film.getId());
            film.setMpa(mpa);
            // Сохраняем ID для БД операций
            if (mpa != null) {
                film.setMpaId(mpa.getId());
            }
        } catch (Exception e) {
            film.setMpa(null);
            film.setMpaId(null);
        }
    }

    private void updateGenres(Film film) {
        jdbc.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
        if (film.getGenreIds() != null && !film.getGenreIds().isEmpty()) {
            for (Long genreId : film.getGenreIds()) {
                jdbc.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)", film.getId(), genreId);
            }
        }
    }

    private void updateLikes(Film film) {
        jdbc.update("DELETE FROM film_likes WHERE film_id = ?", film.getId());
        if (film.getLikes() != null && !film.getLikes().isEmpty()) {
            for (Long userId : film.getLikes()) {
                jdbc.update("INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)", film.getId(), userId);
            }
        }
    }
}
package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
public class GenreDbStorage extends BaseRepository<Genre> {

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public List<Genre> findAll() {
        String sql = "SELECT * FROM genres ORDER BY genre_id";
        return findMany(sql);
    }

    public Optional<Genre> findById(Long id) {
        String sql = "SELECT * FROM genres WHERE genre_id = ?";
        return findOne(sql, id);
    }
}

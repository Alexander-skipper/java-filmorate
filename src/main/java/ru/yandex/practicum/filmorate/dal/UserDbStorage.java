package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("userDbStorage")
@Slf4j
public class UserDbStorage extends BaseRepository<User> implements UserStorage {

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<User> findAll() {
        String sql = "SELECT * FROM users";
        List<User> users = findMany(sql);
        users.forEach(this::loadFriends);
        return users;
    }

    @Override
    public User create(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        long id = insert(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        user.setId(id);
        updateFriends(user);
        return user;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        updateFriends(user);
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        Optional<User> user = findOne(sql, id);
        user.ifPresent(this::loadFriends);
        return user;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        jdbc.update(sql, id);
    }

    @Override
    public void deleteAll() {
        jdbc.update("DELETE FROM friendships");
        jdbc.update("DELETE FROM film_likes");
        jdbc.update("DELETE FROM users");
        jdbc.update("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1");
    }

    @Override
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public void addEmail(String email) {
        throw new UnsupportedOperationException("Method not supported for database storage");
    }

    @Override
    public void removeEmail(String email) {
        throw new UnsupportedOperationException("Method not supported for database storage");
    }

    public void removeFriendOneWay(Long userId, Long friendId) {
        String sql = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
        jdbc.update(sql, userId, friendId);
    }

    public boolean friendshipExists(Long userId, Long friendId) {
        String sql = "SELECT COUNT(*) FROM friendships WHERE user_id = ? AND friend_id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, userId, friendId);
        return count != null && count > 0;
    }

    private void loadFriends(User user) {
        String sql = "SELECT friend_id FROM friendships WHERE user_id = ?";
        List<Long> friendIds = jdbc.queryForList(sql, Long.class, user.getId());
        user.getFriends().clear();
        user.getFriends().addAll(friendIds);
    }

    private void updateFriends(User user) {
        jdbc.update("DELETE FROM friendships WHERE user_id = ?", user.getId());
        for (Long friendId : user.getFriends()) {
            jdbc.update("INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)", user.getId(), friendId);
        }
    }

    public void addFriendOneWay(Long userId, Long friendId) {
        String sql = "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)";
        jdbc.update(sql, userId, friendId);
    }
}

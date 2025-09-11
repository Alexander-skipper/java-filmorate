package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> existingEmails = new HashSet<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        existingEmails.add(user.getEmail());
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    @Override
    public void deleteAll() {
        users.clear();
        existingEmails.clear();
    }

    @Override
    public boolean emailExists(String email) {
        return existingEmails.contains(email);
    }

    @Override
    public void addEmail(String email) {
        existingEmails.add(email);
    }

    @Override
    public void removeEmail(String email) {
        existingEmails.remove(email);
    }

    private Long getNextId() {
        return users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0) + 1;
    }
}

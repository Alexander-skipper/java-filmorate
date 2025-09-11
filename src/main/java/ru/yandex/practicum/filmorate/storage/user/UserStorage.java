package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<User> findAll();

    User create(User user);

    User update(User user);

    Optional<User> findById(Long id);

    void delete(Long id);

    void deleteAll();

    boolean emailExists(String email);

    void addEmail(String email);

    void removeEmail(String email);
}

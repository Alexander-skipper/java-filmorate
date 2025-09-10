package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validation.CreateValidation;
import ru.yandex.practicum.filmorate.validation.UpdateValidation;
import ru.yandex.practicum.filmorate.validation.UserValidator;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@Validated
public class UserController {
    private final UserService userService;
    private final UserValidator userValidator;
    private final UserStorage userStorage;

    @Autowired
    public UserController(UserService userService, UserValidator userValidator, UserStorage userStorage) {
        this.userService = userService;
        this.userValidator = userValidator;
        this.userStorage = userStorage;
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("Получен запрос на получение всех пользователей");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable Long id) {
        log.info("Получен запрос на получение пользователя с id = {}", id);
        return userService.findById(id);
    }

    @PostMapping
    @Validated(CreateValidation.class)
    public User create(@Valid @RequestBody User user) {
        log.info("Получен запрос на создание пользователя: {}", user);
        userValidator.processUserFields(user);
        User createdUser = userService.create(user);
        log.info("Пользователь создан успешно: {}", createdUser);
        return createdUser;
    }

    @PutMapping
    @Validated(UpdateValidation.class)
    public User update(@Valid @RequestBody User user) {
        log.info("Получен запрос на обновление пользователя: {}", user);

        if (user.getId() == null) {
            throw new ValidationException("ID пользователя не может быть null");
        }

        userStorage.findById(user.getId())
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + user.getId() + " не найден"));
        userValidator.processUserFields(user);
        User updatedUser = userService.update(user);
        log.info("Пользователь обновлен успешно: {}", updatedUser);
        return updatedUser;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Получен запрос на добавление в друзья: пользователь {} добавляет пользователя {}", id, friendId);
        userService.addFriend(id, friendId);
        log.info("Друг добавлен успешно");
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Получен запрос на удаление из друзей: пользователь {} удаляет пользователя {}", id, friendId);
        userService.removeFriend(id, friendId);
        log.info("Друг удален успешно");
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        log.info("Получен запрос на получение друзей пользователя {}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Получен запрос на получение общих друзей пользователей {} и {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}

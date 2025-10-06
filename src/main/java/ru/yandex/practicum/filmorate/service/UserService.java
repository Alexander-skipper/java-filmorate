package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        if (userStorage.emailExists(user.getEmail())) {
            throw new ValidationException("Пользователь с email " + user.getEmail() + " уже существует");
        }

        return userStorage.create(user);
    }

    public User update(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        User existingUser = userStorage.findById(user.getId())
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + user.getId() + " не найден"));

        if (!existingUser.getEmail().equals(user.getEmail())) {
            if (userStorage.emailExists(user.getEmail())) {
                throw new ValidationException("Пользователь с email " + user.getEmail() + " уже существует");
            }
            //userStorage.removeEmail(existingUser.getEmail());
            //userStorage.addEmail(user.getEmail());
        }
        return userStorage.update(user);
    }

    public User findById(Long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + id + " не найден"));
    }

    public void addFriend(Long userId, Long friendId) {
        User user = findById(userId);
        User friend = findById(friendId);

        if (userStorage instanceof UserDbStorage) {
            UserDbStorage userDbStorage = (UserDbStorage) userStorage;
            if (!userDbStorage.friendshipExists(userId, friendId)) {
                userDbStorage.addFriendOneWay(userId, friendId);
                log.trace("Пользователь {} добавил пользователя {} в друзья", userId, friendId);
            } else {
                log.trace("Пользователь {} уже в друзьях у {}", friendId, userId);
            }
        } else {
            boolean addedToUser = user.addFriend(friendId);

            if (addedToUser) {
                userStorage.update(user);
                log.trace("Пользователь {} добавил пользователя {} в друзья", userId, friendId);
            } else {
                log.trace("Пользователь {} уже в друзьях у {}", friendId, userId);
            }
        }
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = findById(userId);
        User friend = findById(friendId);

        if (userStorage instanceof UserDbStorage) {
            UserDbStorage userDbStorage = (UserDbStorage) userStorage;
            if (userDbStorage.friendshipExists(userId, friendId)) {
                userDbStorage.removeFriendOneWay(userId, friendId);
                log.trace("Пользователь {} удалил пользователя {} из друзей", userId, friendId);
            } else {
                log.trace("Пользователь {} не был в друзьях у {}", friendId, userId);
            }
        } else {
            boolean removedFromUser = user.removeFriend(friendId);
            if (removedFromUser) {
                userStorage.update(user);
                log.trace("Пользователь {} удалил пользователя {} из друзей", userId, friendId);
            } else {
                log.trace("Пользователь {} не был в друзьях у {}", friendId, userId);
            }
        }
    }

    public List<User> getFriends(Long userId) {
        User user = findById(userId);

        return user.getFriends().stream()
                .map(this::findById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        User user = findById(userId);
        User otherUser = findById(otherId);

        Set<Long> commonFriends = new HashSet<>(user.getFriends());
        commonFriends.retainAll(otherUser.getFriends());

        return commonFriends.stream()
                .map(this::findById)
                .collect(Collectors.toList());
    }
}

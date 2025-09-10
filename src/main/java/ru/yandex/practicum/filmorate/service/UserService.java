package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final Map<Long, Set<Long>> friendships = new HashMap<>();

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User findById(Long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + id + " не найден"));
    }

    public void addFriend(Long userId, Long friendId) {
        User user = findById(userId);
        User friend = findById(friendId);

        Set<Long> userFriends = friendships.getOrDefault(userId, new HashSet<>());
        Set<Long> friendFriends = friendships.getOrDefault(friendId, new HashSet<>());

        if (userFriends.contains(friendId)) {
            throw new ValidationException("Пользователи уже являются друзьями");
        }

        userFriends.add(friendId);
        friendFriends.add(userId);

        friendships.put(userId, userFriends);
        friendships.put(friendId, friendFriends);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = findById(userId);
        User friend = findById(friendId);

        Set<Long> userFriends = friendships.getOrDefault(userId, new HashSet<>());
        Set<Long> friendFriends = friendships.getOrDefault(friendId, new HashSet<>());

        if (!userFriends.contains(friendId)) {
            return;
        }

        userFriends.remove(friendId);
        friendFriends.remove(userId);

        friendships.put(userId, userFriends);
        friendships.put(friendId, friendFriends);
    }

    public List<User> getFriends(Long userId) {
        validateUserExists(userId);

        return friendships.getOrDefault(userId, Collections.emptySet()).stream()
                .map(this::findById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        validateUserExists(userId);
        validateUserExists(otherId);

        Set<Long> userFriends = friendships.getOrDefault(userId, Collections.emptySet());
        Set<Long> otherFriends = friendships.getOrDefault(otherId, Collections.emptySet());

        Set<Long> commonFriends = new HashSet<>(userFriends);
        commonFriends.retainAll(otherFriends);

        return commonFriends.stream()
                .map(this::findById)
                .collect(Collectors.toList());
    }

    private void validateUserExists(Long userId) {
        if (userStorage.findById(userId).isEmpty()) {
            throw new UserNotFoundException("Пользователь с id = " + userId + " не найден");
        }
    }
}

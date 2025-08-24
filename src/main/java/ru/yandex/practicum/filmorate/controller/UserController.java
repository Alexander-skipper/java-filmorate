package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
   private final Map<Long, User> users = new HashMap<>();

   @GetMapping
    public Collection<User> findAll() {
       log.info("Получен запрос на получение всех пользователей. Текущее количество: {}", users.size());
       return  users.values();
   }

   @PostMapping
    public User create(@RequestBody User user) {
       log.info("Получен запрос на создание пользователя: {}", user);
       validateUser(user);
       user.setId(getNextId());

       if (user.getName() == null || user.getName().isBlank()) {
           user.setName(user.getLogin());
       }

       users.put(user.getId(), user);
       log.info("Пользователь создан успешно: {}", user);
       return user;
   }

   @PutMapping
   public User update(@RequestBody User user) {
       log.info("Получен запрос на обновление пользователя: {}", user);

       if (user.getId() == null) {
           log.error("Id пользователя должен быть указан");
           throw new ValidationException("Id должен быть указан");
       }

       if (!users.containsKey(user.getId())) {
           log.error("Пользователь с id = {} не найден", user.getId());
           throw new ValidationException("Пользователь с id = " + user.getId() + " не найден");
       }

       validateUser(user);

       if (user.getName() == null || user.getName().isBlank()) {
           user.setName(user.getLogin());
       }

       users.put(user.getId(), user);
       log.info("Пользователь обновлен успешно: {}", user);
       return user;
   }

   private  void validateUser(User user) {
       if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
           log.error("Электронная почта не может быть пустой и должна содержать @");
           throw new ValidationException("Электронная почта не может быть пустой и должна содержать @");
       }

       if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
           log.error("Логин не может быть пустым и содержать пробелы");
           throw new ValidationException("Логин не может быть пустым и содержать пробелы");
       }

       if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
           log.error("Дата рождения не может быть в будущем");
           throw new ValidationException("Дата рождения не может быть в будущем");
       }
   }

    private Long getNextId() {
        return users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0) + 1;
    }

}

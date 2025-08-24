package ru.yandex.practicum.filmorate.manager;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserControllerTest {
    private UserController userController;
    private User validUser;

    @BeforeEach
    void setUp() {
        userController = new UserController();
        validUser = new User();
        validUser.setEmail("test@example.com");
        validUser.setLogin("testlogin");
        validUser.setName("Test User");
        validUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    void createUser_WithValidData_ShouldSuccess() {
        User createdUser = userController.create(validUser);

        assertNotNull(createdUser.getId());
        assertEquals("test@example.com", createdUser.getEmail());
        assertEquals("testlogin", createdUser.getLogin());
    }

    @Test
    void createUser_WithEmptyEmail_ShouldThrowValidationException() {
        User user = new User();
        user.setEmail("");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void createUser_WithNullEmail_ShouldThrowValidationException() {
        User user = new User();
        user.setEmail(null);
        user.setLogin("login");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void createUser_WithEmailWithoutAt_ShouldThrowValidationException() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void createUser_WithEmptyLogin_ShouldThrowValidationException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void createUser_WithNullLogin_ShouldThrowValidationException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin(null);
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void createUser_WithLoginContainingSpaces_ShouldThrowValidationException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("login with spaces");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void createUser_WithFutureBirthday_ShouldThrowValidationException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void createUser_WithTodayBirthday_ShouldSuccess() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.now());

        User createdUser = userController.create(user);
        assertNotNull(createdUser);
    }

    @Test
    void createUser_WithNullName_ShouldUseLoginAsName() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName(null);
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userController.create(user);
        assertEquals("testlogin", createdUser.getName());
    }

    @Test
    void createUser_WithEmptyName_ShouldUseLoginAsName() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName("");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userController.create(user);
        assertEquals("testlogin", createdUser.getName());
    }

    @Test
    void createUser_WithBlankName_ShouldUseLoginAsName() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName("   ");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userController.create(user);
        assertEquals("testlogin", createdUser.getName());
    }

    @Test
    void updateUser_WithNonExistentId_ShouldThrowValidationException() {
        User user = new User();
        user.setId(999L);
        user.setEmail("test@example.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> userController.update(user));
    }

    @Test
    void updateUser_WithNullId_ShouldThrowValidationException() {
        User user = new User();
        user.setId(null);
        user.setEmail("test@example.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> userController.update(user));
    }

    @Test
    void findAllUsers_AfterCreatingUsers_ShouldReturnCorrectCount() {
        userController.create(validUser);

        User anotherUser = new User();
        anotherUser.setEmail("another@example.com");
        anotherUser.setLogin("anotherlogin");
        anotherUser.setBirthday(LocalDate.of(1995, 1, 1));
        userController.create(anotherUser);

        assertEquals(2, userController.findAll().size());
    }
}


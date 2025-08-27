package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.CreateValidation;
import ru.yandex.practicum.filmorate.validation.UpdateValidation;

import java.time.LocalDate;

@Data
public class User {
    private Long id;

    @NotBlank(message = "Электронная почта не может быть пустой", groups = CreateValidation.class)
    @Email(message = "Электронная почта должна быть корректного формата", groups = {CreateValidation.class,
            UpdateValidation.class})
    private String email;

    @NotBlank(message = "Логин не может быть пустым", groups = CreateValidation.class)
    @Pattern(regexp = "\\S+", message = "Логин не может содержать пробелы", groups = {CreateValidation.class,
            UpdateValidation.class})
    private String login;

    private String name;

    @NotNull(message = "Дата рождения должна быть указана", groups = CreateValidation.class)
    @PastOrPresent(message = "Дата рождения не может быть в будущем", groups = {CreateValidation.class,
            UpdateValidation.class})
    private LocalDate birthday;
}

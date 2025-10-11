package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.CreateValidation;
import ru.yandex.practicum.filmorate.validation.UpdateValidation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Film {

    private Long id;

    @NotBlank(message = "Название не может быть пустым", groups = {CreateValidation.class, UpdateValidation.class})
    private String name;

    @NotBlank(message = "Описание не может быть пустым", groups = CreateValidation.class)
    @Size(max = 200, message = "Максимальная длина описания — 200 символов",
            groups = {CreateValidation.class, UpdateValidation.class})
    private String description;

    @NotNull(message = "Дата релиза должна быть указана", groups = CreateValidation.class)
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность должна быть положительным числом",
            groups = {CreateValidation.class, UpdateValidation.class})
    @Min(value = 1, message = "Продолжительность должна быть не менее 1 минуты",
            groups = {CreateValidation.class, UpdateValidation.class})
    private Integer duration;


    @JsonIgnore
    private Long mpaId;

    private Mpa mpa;

    @JsonIgnore
    private Set<Long> genreIds = new HashSet<>();

    private List<Genre> genres = new ArrayList<>();

}

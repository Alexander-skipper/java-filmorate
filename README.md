# java-filmorate
Template repository for Filmorate project.
# Filmorate - База данных

## Схема базы данных

![Диаграмма базы данных](database_schema.png)

*Рисунок 1: Логическая схема базы данных Filmorate*

## Описание схемы

База данных Filmorate состоит из 7 основных таблиц, спроектированных для хранения информации о фильмах, пользователях и их взаимодействиях.

### Основные таблицы:
- **users** - регистрационные данные пользователей
- **films** - информация о фильмах с метаданными  
- **mpa_ratings** - справочник возрастных рейтингов MPA
- **genres** - справочник жанров фильмов

### Таблицы связей:
- **film_genres** - связь многие-ко-многим между фильмами и жанрами
- **film_likes** - учет лайков пользователей фильмам
- **friendships** - система дружбы между пользователями с подтверждением

## Примеры SQL запросов

### 1. Получение топ-10 популярных фильмов
```sql
SELECT f.film_id, f.name, COUNT(fl.user_id) as likes_count
FROM films f
LEFT JOIN film_likes fl ON f.film_id = fl.film_id
GROUP BY f.film_id, f.name
ORDER BY likes_count DESC
LIMIT 10;
```

### 2. Добавление нового пользователя
```sql
INSERT INTO users (email, login, name, birthday)
VALUES ('user@example.com', 'user123', 'Иван Иванов', '1990-01-01');
```

### 3. Добавление фильма с жанрами
```sql
-- Добавляем фильм
INSERT INTO films (name, description, release_date, duration, mpa_id)
VALUES ('Интерстеллар', 'Фантастика о космических путешествиях', '2014-10-26', 169, 3);

-- Связываем с жанрами
INSERT INTO film_genres (film_id, genre_id) VALUES 
(LAST_INSERT_ID(), 2), -- Драма
(LAST_INSERT_ID(), 4); -- Триллер
```

### 4. Получение общих друзей двух пользователей
```sql
SELECT u.user_id, u.name, u.login
FROM friendships f1
JOIN friendships f2 ON f1.user_id2 = f2.user_id2
JOIN users u ON f1.user_id2 = u.user_id
WHERE f1.user_id1 = 1 AND f2.user_id1 = 2
AND f1.status = 'confirmed' AND f2.status = 'confirmed';
```

### 5. Получение фильмов по жанру
```sql
SELECT f.film_id, f.name, f.release_date, g.name as genre
FROM films f
JOIN film_genres fg ON f.film_id = fg.film_id
JOIN genres g ON fg.genre_id = g.genre_id
WHERE g.name = 'Комедия'
ORDER BY f.release_date DESC;
```

### 6. Добавление лайка фильму
```sql
INSERT INTO film_likes (film_id, user_id)
VALUES (1, 1);
```

### 7. Подтверждение дружбы
```sql
UPDATE friendships 
SET status = 'confirmed' 
WHERE user_id1 = 2 AND user_id2 = 1;
```

## Основные операции приложения

- **Регистрация пользователя** - INSERT в таблицу `users`
- **Добавление фильма** - INSERT в таблицу `films` + `film_genres`
- **Добавление в друзья** - INSERT в таблицу `friendships`
- **Лайк фильма** - INSERT в таблицу `film_likes`
- **Поиск общих друзей** - JOIN по таблице `friendships`
- **Топ фильмов** - GROUP BY + COUNT по `film_likes`
- **Фильтрация по жанру** - JOIN через `film_genres`

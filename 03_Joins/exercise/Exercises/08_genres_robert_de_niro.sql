-- 8. The genres of movies that Robert De Niro has appeared in that were released in 2010 or later, sorted alphabetically.
-- (6 rows)

SELECT DISTINCT genre_name
FROM genre
JOIN movie_genre ON genre.genre_id = movie_genre.genre_id
JOIN movie_actor ON movie_genre.movie_id = movie_actor.movie_id
JOIN movie ON movie_actor.movie_id = movie.movie_id
JOIN person ON movie_actor.actor_id = person_id
WHERE person_name = 'Robert De Niro' AND movie.release_date > '2010-01-01'
ORDER BY genre_name;


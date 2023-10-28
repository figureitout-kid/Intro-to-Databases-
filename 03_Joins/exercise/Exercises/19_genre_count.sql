-- 19. The genre name and the number of movies in each genre. Name the count column 'num_of_movies'.
-- Order the results from the highest movie count to the lowest.
-- (19 rows, the highest expected count is around 400).

SELECT genre_name, COUNT(genre.genre_id) AS num_of_movies
FROM genre
JOIN movie_genre ON genre.genre_id = movie_genre.genre_id
LEFT JOIN movie ON genre.genre_id = movie.movie_id
GROUP BY genre_name
ORDER BY num_of_movies DESC;


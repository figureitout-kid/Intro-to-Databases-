package com.techelevator.movies.dao;

import com.techelevator.movies.model.Collection;
import com.techelevator.movies.model.Movie;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JdbcMovieDao implements MovieDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcMovieDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Movie> getMovies() {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT movie_id, title, overview, tagline, poster_path, home_page, release_date, length_minutes, " +
                "director_id, collection_id FROM movie;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            Movie movie = new Movie();

            movie.setId(results.getInt("movie_id"));
            movie.setTitle(results.getString("title"));
            movie.setOverview(results.getString("overview"));
            movie.setTagline(results.getString("tagline"));
            movie.setPosterPath(results.getString("poster_path"));
            movie.setHomePage(results.getString("home_page"));

            movie.setReleaseDate(results.getDate("release_date").toLocalDate());
            movie.setLengthMinutes(results.getInt("length_minutes"));
            movie.setDirectorId(results.getInt("director_id"));
            movie.setCollectionId(results.getInt("collection_id"));

            movies.add(movie);
        }
        return movies;
    }


    @Override
    public Movie getMovieById(int id) {
        String sql = "SELECT movie_id, title, overview, tagline, poster_path, home_page, release_date, length_minutes, " +
                "director_id, collection_id FROM movie WHERE movie_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);

        if (results.next()) {
            Movie movie = new Movie();
            movie.setId(results.getInt("movie_id"));
            movie.setTitle(results.getString("title"));
            movie.setOverview(results.getString("overview"));
            movie.setTagline(results.getString("tagline"));
            movie.setPosterPath(results.getString("poster_path"));
            movie.setHomePage(results.getString("home_page"));

            movie.setReleaseDate(results.getDate("release_date").toLocalDate());
            movie.setLengthMinutes(results.getInt("length_minutes"));
            movie.setDirectorId(results.getInt("director_id"));
            movie.setCollectionId(results.getInt("collection_id"));

            return movie;
        } else {
            return null;
        }
    }

    @Override
    public List<Movie> getMoviesByTitle(String title, boolean useWildCard) {
        List<Movie> movies = new ArrayList<>();
            if (useWildCard) {
                title = "%" + title + "%";
            }
            String sql = sql = "SELECT movie_id, title, overview, tagline, poster_path, home_page, release_date, length_minutes, " +
                    "director_id, collection_id FROM movie WHERE title ILIKE ?;";
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, title);

            while (results.next()) {
                Movie movie = new Movie();
                movie.setId(results.getInt("movie_id"));
                movie.setTitle(results.getString("title"));
                movie.setOverview(results.getString("overview"));
                movie.setTagline(results.getString("tagline"));
                movie.setPosterPath(results.getString("poster_path"));
                movie.setHomePage(results.getString("home_page"));

                movie.setReleaseDate(results.getDate("release_date").toLocalDate());
                movie.setLengthMinutes(results.getInt("length_minutes"));
                movie.setDirectorId(results.getInt("director_id"));
                movie.setCollectionId(results.getInt("collection_id"));

                movies.add(movie);
            }
            return movies;
    }

    @Override
    public List<Movie> getMoviesByDirectorNameBetweenYears(String directorName, int startYear,
           int endYear, boolean useWildCard) {
        List<Movie> movies = new ArrayList<>();

        String sql;
        if (useWildCard) {
            sql = "SELECT m.* " +
                    "FROM movie m " +
                    "INNER JOIN person p ON m.director_id = p.person_id " +
                    "WHERE p.person_name ILIKE ? " +
                    "AND EXTRACT(YEAR FROM m.release_date) BETWEEN ? AND ?";
            directorName = "%" + directorName + "%";
        } else {
            sql = "SELECT m.* " +
                    "FROM movie m " +
                    "INNER JOIN person p ON m.director_id = p.person_id " +
                    "WHERE p.person_name = ? " +
                    "AND EXTRACT(YEAR FROM m.release_date) BETWEEN ? AND ?";
        }

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, directorName, startYear, endYear);
        while (results.next()) {
            Movie movie = new Movie();
            movie.setId(results.getInt("movie_id"));
            movie.setTitle(results.getString("title"));
            movie.setOverview(results.getString("overview"));
            movie.setTagline(results.getString("tagline"));
            movie.setPosterPath(results.getString("poster_path"));
            movie.setHomePage(results.getString("home_page"));

            movie.setReleaseDate(results.getDate("release_date").toLocalDate());
            movie.setLengthMinutes(results.getInt("length_minutes"));
            movie.setDirectorId(results.getInt("director_id"));
            movie.setCollectionId(results.getInt("collection_id"));

            movies.add(movie);
        }
        return movies;

    }
}

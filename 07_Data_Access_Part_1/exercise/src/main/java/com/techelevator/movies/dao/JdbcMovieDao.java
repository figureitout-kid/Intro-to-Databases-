package com.techelevator.movies.dao;

import com.techelevator.movies.model.Movie;
import org.springframework.jdbc.core.JdbcTemplate;

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
        return null;
    }

    @Override
    public Movie getMovieById(int id) {
        return new Movie(-1, "Not implemented yet", "", "", "", "", null, 0, -1, -1);
    }

    @Override
    public List<Movie> getMoviesByTitle(String title, boolean useWildCard) {
        return null;
    }

    @Override
    public List<Movie> getMoviesByDirectorNameBetweenYears(String directorName, int startYear,
           int endYear, boolean useWildCard) {
        return null;
    }
}

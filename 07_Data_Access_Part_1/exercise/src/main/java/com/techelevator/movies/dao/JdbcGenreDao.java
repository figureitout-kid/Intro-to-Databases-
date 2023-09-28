package com.techelevator.movies.dao;

import com.techelevator.movies.model.Genre;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JdbcGenreDao implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcGenreDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Genre> getGenres() {
        return null;
    }

    @Override
    public Genre getGenreById(int id) {
        return new Genre(-1, "Not implemented yet");
    }

    @Override
    public List<Genre> getGenresByName(String name, boolean useWildCard) {
        return null;
    }
}

package com.techelevator.movies.dao;

import com.techelevator.movies.model.Person;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JdbcPersonDao implements PersonDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcPersonDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Person> getPersons() {
        return null;
    }

    @Override
    public Person getPersonById(int id) {
        return new Person(-1, "Not implemented yet", null, null, "", "", "");
    }

    @Override
    public List<Person> getPersonsByName(String name, boolean useWildCard) {
        return null;
    }

    @Override
    public List<Person> getPersonsByCollectionName(String collectionName, boolean useWildCard) {
        return null;
    }
}

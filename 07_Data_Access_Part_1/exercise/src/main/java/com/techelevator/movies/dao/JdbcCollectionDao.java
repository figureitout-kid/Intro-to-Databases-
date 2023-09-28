package com.techelevator.movies.dao;

import com.techelevator.movies.model.Collection;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JdbcCollectionDao implements CollectionDao{

    private final JdbcTemplate jdbcTemplate;

    public JdbcCollectionDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Collection> getCollections() {
        return null;
    }

    @Override
    public Collection getCollectionById(int id) {
        return new Collection(-1, "Not implemented yet");
    }

    @Override
    public List<Collection> getCollectionsByName(String name, boolean useWildCard) {
        return null;
    }
}

package com.techelevator.movies.dao;

import com.techelevator.movies.model.Collection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

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
        List<Collection> collections = new ArrayList<>();
        String sql = "SELECT collection_id, collection_name FROM collection;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            Collection collection = new Collection();
            collection.setId(results.getInt("collection_id"));
            collection.setName(results.getString("collection_name"));
            collections.add(collection);
        }
        return collections;
    }

    @Override
    public Collection getCollectionById(int id) {
        String sql = "SELECT collection_id, collection_name FROM collection WHERE collection_id = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);

        if (result.next()) {
            Collection collection = new Collection();
            collection.setId(result.getInt("collection_id"));
            collection.setName(result.getString("collection_name"));
            return collection;
        } else {
            return null;
        }
    }


    @Override
    public List<Collection> getCollectionsByName(String name, boolean useWildCard) {
    List<Collection> collections = new ArrayList<>();
    if (useWildCard) {
        name = "%" + name + "%";
    }
    String sql = sql = "SELECT collection_id, collection_name FROM collection WHERE collection_name ILIKE ?;";
    SqlRowSet results = jdbcTemplate.queryForRowSet(sql, name);

    while (results.next()) {
        Collection collection = new Collection();
        collection.setId(results.getInt("collection_id"));
        collection.setName(results.getString("collection_name"));
        collections.add(collection);
    }
    return collections;
    }
}

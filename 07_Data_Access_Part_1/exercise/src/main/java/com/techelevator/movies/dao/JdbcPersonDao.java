package com.techelevator.movies.dao;

import com.techelevator.movies.model.Person;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

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
        List<Person> persons = new ArrayList<>();

        String sql = "SELECT person_id, person_name, birthday, deathday, biography, profile_path, home_page " +
                "FROM person;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);

        while(results.next()) {
        Person person = new Person();

        person.setId(results.getInt("person_id"));
        person.setName(results.getString("person_name"));

        java.sql.Date birthdayDate = results.getDate("birthday");
        if (birthdayDate != null) person.setBirthday(birthdayDate.toLocalDate());
//        person.setBirthday(results.getDate("birthday").toLocalDate());
//        person.setDeathDate(results.getDate("deathday").toLocalDate());
//
        java.sql.Date deathDate = results.getDate("deathday");
        if (deathDate != null) person.setDeathDate(deathDate.toLocalDate());

        person.setBiography(results.getString("biography"));
        person.setProfilePath(results.getString("profile_path"));
        person.setHomePage(results.getString("home_page"));

        persons.add(person);
        }
        return persons;
    }

    @Override
    public Person getPersonById(int id) {
        String sql = "SELECT person_id, person_name, birthday, deathday, biography, profile_path, home_page " +
                "FROM person " +
                "WHERE person_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);

        if (results.next()){
            Person person = new Person();

            person.setId(results.getInt("person_id"));
            person.setName(results.getString("person_name"));

            java.sql.Date birthdayDate = results.getDate("birthday");
            if (birthdayDate != null) person.setBirthday(birthdayDate.toLocalDate());
            java.sql.Date deathDate = results.getDate("deathday");
            if (deathDate != null) person.setDeathDate(deathDate.toLocalDate());

            person.setBiography(results.getString("biography"));
            person.setProfilePath(results.getString("profile_path"));
            person.setHomePage(results.getString("home_page"));

            return person;
        }
        else return null;
    }

    @Override
    public List<Person> getPersonsByName(String name, boolean useWildCard) {
       List<Person> persons = new ArrayList<>();
       if (useWildCard)
           name = "%" + name + "%";
       String sql = "SELECT person_id, person_name, birthday, deathday, biography, profile_path, home_page " +
               "FROM person " +
               "WHERE person_name ILIKE ?;";
       SqlRowSet results = jdbcTemplate.queryForRowSet(sql, name);

       while(results.next()) {
            Person person = new Person();

            person.setId(results.getInt("person_id"));
            person.setName(results.getString("person_name"));

            java.sql.Date birthdayDate = results.getDate("birthday");
            if (birthdayDate != null) person.setBirthday(birthdayDate.toLocalDate());

            java.sql.Date deathDate = results.getDate("deathday");
            if (deathDate != null) person.setDeathDate(deathDate.toLocalDate());

            person.setBiography(results.getString("biography"));
            person.setProfilePath(results.getString("profile_path"));
            person.setHomePage(results.getString("home_page"));

            persons.add(person);
        }
        return persons;
    }


    @Override
    public List<Person> getPersonsByCollectionName(String collectionName, boolean useWildCard) {
        List<Person> persons = new ArrayList<>();
        String sql;
        if (useWildCard) {
            sql = "SELECT DISTINCT p.* " +
                "FROM person p " +
                "JOIN movie_actor ma ON p.person_id = ma.actor_id " +
                "JOIN movie m ON ma.movie_id = m.movie_id " +
                "JOIN collection c ON m.collection_id = c.collection_id " +
                "WHERE c.collection_name ILIKE ?;";
            collectionName = "%" + collectionName + "%";
        } else {
             sql = "SELECT DISTINCT p.* " +
                    "FROM person p " +
                    "JOIN movie_actor ma ON p.person_id = ma.actor_id " +
                    "JOIN movie m ON ma.movie_id = m.movie_id " +
                    "JOIN collection c ON m.collection_id = c.collection_id " +
                    "WHERE c.collection_name = ?;";
        }

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, collectionName);
        while(results.next()) {
            Person person = new Person();

            person.setId(results.getInt("person_id"));
            person.setName(results.getString("person_name"));

            java.sql.Date birthdayDate = results.getDate("birthday");
            if (birthdayDate != null) person.setBirthday(birthdayDate.toLocalDate());

            java.sql.Date deathDate = results.getDate("deathday");
            if (deathDate != null) person.setDeathDate(deathDate.toLocalDate());

            person.setBiography(results.getString("biography"));
            person.setProfilePath(results.getString("profile_path"));
            person.setHomePage(results.getString("home_page"));

            persons.add(person);
        }
        return persons;
    }
}

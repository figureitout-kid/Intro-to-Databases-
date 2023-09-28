package com.techelevator.movies.dao;

import com.techelevator.movies.model.Genre;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JdbcGenreDaoTest extends BaseDaoTest {

    private static final Genre GENRE_878 =
            new Genre(878, "Science Fiction");

    private JdbcGenreDao sut;

    @Before
    public void setup() {
        sut = new JdbcGenreDao(dataSource);
    }

    @Test
    public void getGenres_returns_correct_number_of_Genres() {

        List<Genre> genres = sut.getGenres();
        assertNotNull("getGenres unexpectedly returned null", genres);
        assertNotEquals("getGenres unexpectedly returned an empty list of genres",
                0, genres.size());
        assertEquals("getGenres did not return the correct number of genres",
                19, genres.size());
    }

    @Test
    public void getGenreById_returns_correct_Genre_for_id() {

        Genre genre = sut.getGenreById(878);
        assertNotNull("getGenresById unexpectedly returned null", genre);
        assertGenresMatch("getGenreById returned wrong or partial data", GENRE_878, genre);
    }

    @Test
    public void getGenreById_returns_null_for_unknown_id() {

        Genre genre = sut.getGenreById(0); // IDs begin with 1, cannot be 0
        assertNull("getGenreById unexpectedly returned Genre", genre);
    }

    @Test
    public void getGenresByName_exact_match_returns_correct_genres() {

        List<Genre> genres = sut.getGenresByName("Science Fiction",
                false);
        assertNotNull("getGenresByName unexpectedly returned null", genres);
        assertNotEquals("getGenresByName unexpectedly returned an empty list of genres",
                0, genres.size());
        assertEquals("getGenresByName did not return the correct number of genres",
                1, genres.size());
        assertGenresMatch("getGenresByName returned wrong or partial data", GENRE_878,
                genres.get(0));
    }

    @Test
    public void getGenresByName_wildcard_match_returns_correct_genres() {

        List<Genre> genres = sut.getGenresByName("ience Fict",
                true);
        assertNotNull("getGenresByName unexpectedly returned null", genres);
        assertNotEquals("getGenresByName unexpectedly returned an empty list of genres",
                0, genres.size());
        assertEquals("getGenresByName did not return the correct number of genres",
                1, genres.size());
        assertGenresMatch("getGenresByName returned wrong or partial data", GENRE_878,
                genres.get(0));
    }

    @Test
    public void getGenresByName_single_character_match_with_wildcard_returns_correct_genres() {

        List<Genre> genres = sut.getGenresByName("Y",true);
        assertNotNull("getGenresByName unexpectedly returned null", genres);
        assertEquals("getGenresByName did not return the correct number of genres",
                6, genres.size());
    }

    @Test
    public void getGenresByName_inexact_match_without_wildcard_returns_no_genres() {

        List<Genre> genres = sut.getGenresByName("ience Fict",
                false);
        assertNotNull("getGenresByName unexpectedly returned null", genres);
        assertEquals("getGenresByName did not return the correct number of genres",
                0, genres.size());
    }

    private void assertGenresMatch(String message, Genre expected, Genre actual) {

        assertEquals(message, expected.getId(), actual.getId());
        assertEquals(message, expected.getName(), actual.getName());
    }
}

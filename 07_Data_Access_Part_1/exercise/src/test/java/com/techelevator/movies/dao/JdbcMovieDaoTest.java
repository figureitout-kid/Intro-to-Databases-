package com.techelevator.movies.dao;

import com.techelevator.movies.model.Movie;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JdbcMovieDaoTest extends BaseDaoTest {

    private final Movie MOVIE_311 =
            new Movie(311,
                    "Once Upon a Time in America",
                    "A former Prohibition-era Jewish gangster returns to the Lower East Side of Manhattan over thirty years later, where he once again must confront the ghosts and regrets of his old life.",
                    "Crime, passion and lust for power.",
                    "https://image.tmdb.org/t/p/w500/i0enkzsL5dPeneWnjl1fCWm6L7k.jpg",
                    null,
                    LocalDate.of(1984, 5, 23),
                    229,
                    4385,
                    0
                    );

    private JdbcMovieDao sut;

    @Before
    public void setup() {
        sut = new JdbcMovieDao(dataSource);
    }

    @Test
    public void getMovies_returns_correct_number_of_Movies() {

        List<Movie> movies = sut.getMovies();
        assertNotNull("getMovies unexpectedly returned null", movies);
        assertNotEquals("getMovies unexpectedly returned an empty list of movies",
                0, movies.size());
        assertEquals("getMovies did not return the correct number of movies",
                44, movies.size());
    }

    @Test
    public void getMovieById_returns_correct_Movie_for_id() {

        Movie movie = sut.getMovieById(311);
        assertNotNull("getMovieById unexpectedly returned null", movie);
        assertMoviesMatch("getMovieById returned wrong or partial data", MOVIE_311, movie);
    }

    @Test
    public void getMovieById_returns_null_for_unknown_id() {

        Movie movie = sut.getMovieById(0); // IDs begin with 1, cannot be 0
        assertNull("getMovieById unexpectedly returned Movie", movie);
    }

    @Test
    public void getMoviesByTitle_exact_match_returns_correct_movies() {

        List<Movie> movies = sut.getMoviesByTitle("Once upon a time in America",
                false);
        assertNotNull("getMoviesByTitle unexpectedly returned null", movies);
        assertNotEquals("getMoviesByTitle unexpectedly returned an empty list of movies",
                0, movies.size());
        assertEquals("getMoviesByTitle did not return the correct number of movies",
                1, movies.size());
        assertMoviesMatch("getMoviesByTitle returned wrong or partial data", MOVIE_311,
                movies.get(0));
    }

    @Test
    public void getMoviesByTitle_wildcard_match_returns_correct_movies() {

        List<Movie> movies = sut.getMoviesByTitle("upon a time",
                true);
        assertNotNull("getMoviesByTitle unexpectedly returned null", movies);
        assertNotEquals("getMoviesByTitle unexpectedly returned an empty list of movies",
                0, movies.size());
        assertEquals("getMoviesByTitle did not return the correct number of movies",
                1, movies.size());
    }

    @Test
    public void getMoviesByTitle_single_character_match_with_wildcard_returns_correct_movies() {

        List<Movie> movies = sut.getMoviesByTitle("w",true);
        assertNotNull("getMoviesByTitle unexpectedly returned null", movies);
        assertEquals("getMoviesByTitle did not return the correct number of movies",
                13, movies.size());
    }

    @Test
    public void getMoviesByTitle_inexact_match_without_wildcard_returns_no_movies() {

        List<Movie> movies = sut.getMoviesByTitle("upon a time",
                false);
        assertNotNull("getMoviesByTitle unexpectedly returned null", movies);
        assertEquals("getMoviesByTitle did not return the correct number of movies",
                0, movies.size());
    }

    @Test
    public void getMoviesByDirectorNameBetweenYears_returns_correct_movies() {

        List<Movie> movies = sut.getMoviesByDirectorNameBetweenYears(
                "Alfred Hitchcock", 1950, 1959, true);
        assertNotNull("getMoviesByDirectorNameBetweenYears unexpectedly returned null", movies);
        assertEquals("getMoviesByDirectorNameBetweenYears did not return the correct number of movies",
                7, movies.size());
    }

    private void assertMoviesMatch(String message, Movie expected, Movie actual) {

        assertEquals(message, expected.getId(), actual.getId());
        assertEquals(message, expected.getTitle(), actual.getTitle());
        assertEquals(message, expected.getTagline(), actual.getTagline());
        assertEquals(message, expected.getPosterPath(), actual.getPosterPath());
        assertEquals(message, expected.getHomePage(), actual.getHomePage());
        assertEquals(message, expected.getReleaseDate(), actual.getReleaseDate());
        assertEquals(message, expected.getLengthMinutes(), actual.getLengthMinutes());
        assertEquals(message, expected.getDirectorId(), actual.getDirectorId());
        assertEquals(message, expected.getCollectionId(), actual.getCollectionId());
    }
}

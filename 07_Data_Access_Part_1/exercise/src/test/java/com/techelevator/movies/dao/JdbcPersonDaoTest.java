package com.techelevator.movies.dao;

import com.techelevator.movies.model.Person;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JdbcPersonDaoTest extends BaseDaoTest {

    private static final Person PERSON_1 =
            new Person(1,
                    "George Lucas",
                    LocalDate.of(1944, 5, 14),
                    null,
                    "George Walton Lucas Jr. (born May 14, 1944) is an American filmmaker and entrepreneur. Lucas is known for creating the Star Wars and Indiana Jones franchises and founding Lucasfilm, LucasArts and Industrial Light & Magic. He served as chairman of Lucasfilm before selling it to The Walt Disney Company in 2012.\n\n" +
                            "After graduating from the University of Southern California in 1967, Lucas co-founded American Zoetrope with filmmaker Francis Ford Coppola. Lucas wrote and directed THX 1138 (1971), based on his earlier student short Electronic Labyrinth: THX 1138 4EB, which was a critical success but a financial failure. His next work as a writer-director was the film American Graffiti (1973), inspired by his youth in early 1960s Modesto, California, and produced through the newly founded Lucasfilm. The film was critically and commercially successful, and received five Academy Award nominations including Best Picture.\n\n" +
                            "Lucas's next film, the epic space opera Star Wars (1977), had a troubled production but was a surprise hit, becoming the highest-grossing film at the time, winning six Academy Awards and sparking a cultural phenomenon. Lucas produced and co-wrote the sequels The Empire Strikes Back (1980) and Return of the Jedi (1983). With director Steven Spielberg, he created, produced and co-wrote the Indiana Jones films Raiders of the Lost Ark (1981), Temple of Doom (1984), The Last Crusade (1989) and Kingdom of the Crystal Skull (2008). He also produced and wrote a variety of films and television series through Lucasfilm between the 1970s and the 2010s.\n\n" +
                            "In 1997, Lucas rereleased the Star Wars trilogy as part of a special edition featuring several alterations; home media versions with further changes were released in 2004 and 2011. He returned to directing with a Star Wars prequel trilogy comprising The Phantom Menace (1999), Attack of the Clones (2002), and Revenge of the Sith (2005). He last collaborated on the CGI-animated television series Star Wars: The Clone Wars (2008–2014, 2020), the war film Red Tails (2012), and the CGI film Strange Magic (2015).\n\n" +
                            "Lucas is one of history's most financially successful filmmakers and has been nominated for four Academy Awards. His films are among the 100 highest-grossing movies at the North American box office, adjusted for ticket-price inflation. Lucas is considered a significant figure of the 20th-century New Hollywood movement.\n\n" +
                            "Description above from the Wikipedia article George Lucas, licensed under CC-BY-SA, full list of contributors on Wikipedia.",
                    "https://image.tmdb.org/t/p/w185/WCSZzWdtPmdRxH9LUCVi2JPCSJ.jpg",
                    null
            );

    private JdbcPersonDao sut;

    @Before
    public void setup() {
        sut = new JdbcPersonDao(dataSource);
    }

    @Test
    public void getPersons_returns_correct_number_of_Persons() {

        List<Person> persons = sut.getPersons();
        assertNotNull("getPersons unexpectedly returned null", persons);
        assertNotEquals("getPersons unexpectedly returned an empty list of persons",
                0, persons.size());
        assertEquals("getPersons did not return the correct number of persons",
                331, persons.size());
    }

    @Test
    public void getPersonById_returns_correct_Movie_for_id() {

        Person person = sut.getPersonById(1);
        assertNotNull("getPersonById unexpectedly returned null", person);
        assertPersonsMatch("getPersonById returned wrong or partial data", PERSON_1, person);
    }

    @Test
    public void getPersonById_returns_null_for_unknown_id() {

        Person person = sut.getPersonById(0); // IDs begin with 1, cannot be 0
        assertNull("getPersonById unexpectedly returned Person", person);
    }

    @Test
    public void getPersonsByName_exact_match_returns_correct_persons() {

        List<Person> persons = sut.getPersonsByName("George Lucas",
                false);
        assertNotNull("getPersonsByName unexpectedly returned null", persons);
        assertNotEquals("getPersonsByName unexpectedly returned an empty list of persons",
                0, persons.size());
        assertEquals("getPersonsByName did not return the correct number of persons",
                1, persons.size());
        assertPersonsMatch("getPersonsByName returned wrong or partial data", PERSON_1,
                persons.get(0));
    }

    @Test
    public void getPersonsByName_wildcard_match_returns_correct_persons() {

        List<Person> persons = sut.getPersonsByName("rge Luc",
                true);
        assertNotNull("getPersonsByName unexpectedly returned null", persons);
        assertNotEquals("getPersonsByName unexpectedly returned an empty list of persons",
                0, persons.size());
        assertEquals("getPersonsByName did not return the correct number of persons",
                1, persons.size());
    }

    @Test
    public void getPersonsByName_single_character_match_with_wildcard_returns_correct_movies() {

        List<Person> persons = sut.getPersonsByName("g",true);
        assertNotNull("getPersonsByName unexpectedly returned null", persons);
        assertEquals("getPersonsByName did not return the correct number of persons",
                77, persons.size());
    }

    @Test
    public void getPersonsByName_inexact_match_without_wildcard_returns_no_persons() {

        List<Person> persons = sut.getPersonsByName("rge Luc",
                false);
        assertNotNull("getMoviesByTitle unexpectedly returned null", persons);
        assertEquals("getMoviesByTitle did not return the correct number of movies",
                0, persons.size());
    }

    @Test
    public void getPersonsByCollectionName__returns_correct_persons() {

        List<Person> persons = sut.getPersonsByCollectionName("Star Wars Collection",
                false);
        assertNotNull("getMoviesByTitle unexpectedly returned null", persons);
        assertEquals("getMoviesByTitle did not return the correct number of movies",
                25, persons.size());
    }

    private void assertPersonsMatch(String message, Person expected, Person actual) {

        assertEquals(message, expected.getId(), actual.getId());
        assertEquals(message, expected.getName(), actual.getName());
        assertEquals(message, expected.getBirthday(), actual.getBirthday());
        assertEquals(message, expected.getDeathDate(), actual.getDeathDate());
        assertEquals(message, expected.getBiography(), actual.getBiography());
        assertEquals(message, expected.getProfilePath(), actual.getProfilePath());
        assertEquals(message, expected.getHomePage(), actual.getHomePage());
    }
}

package com.techelevator.movies.dao;

import com.techelevator.movies.model.Collection;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JdbcCollectionDaoTest extends BaseDaoTest {

    private final Collection COLLECTION_86311 =
            new Collection(86311, "The Avengers Collection");

    private JdbcCollectionDao sut;

    @Before
    public void setup() {
        sut = new JdbcCollectionDao(dataSource);
    }

    @Test
    public void getCollections_returns_correct_number_of_Collections() {

        List<Collection> collections = sut.getCollections();
        assertNotNull("getCollections unexpectedly returned null", collections);
        assertNotEquals("getCollections unexpectedly returned an empty list of collections",
                0, collections.size());
        assertEquals("getCollections did not return the correct number of collections",
                6, collections.size());
    }

    @Test
    public void getCollectionById_returns_correct_Collection_for_id() {

        Collection collection = sut.getCollectionById(86311);
        assertNotNull("getCollectionById unexpectedly returned null", collection);
        assertCollectionsMatch("getCollectionById returned wrong or partial data", COLLECTION_86311, collection);
    }

    @Test
    public void getCollectionById_returns_null_for_unknown_id() {

        Collection collection = sut.getCollectionById(0); // IDs begin with 1, cannot be 0
        assertNull("getCollectionById unexpectedly returned Collection", collection);
    }

    @Test
    public void getCollectionByName_exact_match_returns_correct_Collections() {

        List<Collection> collections = sut.getCollectionsByName("the avengers collection",
                false);
        assertNotNull("getCollectionsByName unexpectedly returned null", collections);
        assertNotEquals("getCollectionsByName unexpectedly returned an empty list of collections",
                0, collections.size());
        assertEquals("getCollectionsByName did not return the correct number of collections",
                1, collections.size());
        assertCollectionsMatch("getCollectionByName returned wrong or partial data", COLLECTION_86311,
                collections.get(0));
    }

    @Test
    public void getCollectionByName_wildcard_match_returns_correct_Collections() {

        List<Collection> collections = sut.getCollectionsByName("e avengers c",
                true);
        assertNotNull("getCollectionsByName unexpectedly returned null", collections);
        assertNotEquals("getCollectionsByName unexpectedly returned an empty list of collections",
                0, collections.size());
        assertEquals("getCollectionsByName did not return the correct number of collections",
                1, collections.size());
        assertCollectionsMatch("getCollectionByName returned wrong or partial data", COLLECTION_86311,
                collections.get(0));
    }

    @Test
    public void getCollectionByName_single_character_match_with_wildcard_returns_correct_Collections() {

        List<Collection> collections = sut.getCollectionsByName("F",
                true);
        assertNotNull("getCollectionsByName unexpectedly returned null", collections);
        assertEquals("getCollectionsByName did not return the correct number of collections",
                2, collections.size());
    }

    @Test
    public void getCollectionByName_inexact_match_without_wildcard_returns_no_Collections() {

        List<Collection> collections = sut.getCollectionsByName("e avengers c",
                false);
        assertNotNull("getCollectionsByName unexpectedly returned null", collections);
        assertEquals("getCollectionsByName did not return the correct number of collections",
                0, collections.size());
    }

    private void assertCollectionsMatch(String message, Collection expected, Collection actual) {

        assertEquals(message, expected.getId(), actual.getId());
        assertEquals(message, expected.getName(), actual.getName());
    }
}

package com.techelevator.dao;

import com.techelevator.model.Customer;

import java.util.List;

public interface CustomerDao {

    // Step Four: Add a new DAO method
    Customer getCustomerById(int customerId);
    /**
     * Get a customer from the datastore that has the given id.
     * If the id is not found, return null.
     *
     * @param customerId the id of the customer to retrieve
     * @return a complete Customer object
     */


    /**
     * Get customers whose first or last names include the given search string.
     *
     * @param search the string to search for in customer names
     * @param useWildCard the boolean to control whether to wrap the search term with wild-card characters
     * @return a List of Customer objects
     */
    List<Customer> getCustomersByName(String search, boolean useWildCard);
}

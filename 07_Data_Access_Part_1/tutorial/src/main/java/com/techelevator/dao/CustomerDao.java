package com.techelevator.dao;

import com.techelevator.model.Customer;

import java.util.List;

public interface CustomerDao {

    // Step Four: Add a new DAO method
    Customer getCustomerById(int customerId);

    /**
     * Get customers whose first or last names include the given search string.
     * @param customerId the id of the customer to retrieve
     * @return a complete Customer object
     * @param search the string to search for in customer names
     * @param useWildCard the boolean to control whether to wrap the search term with wild-card characters
     * @return a List of Customer objects
     */
    List<Customer> getCustomersByName(String search, boolean useWildCard);

}

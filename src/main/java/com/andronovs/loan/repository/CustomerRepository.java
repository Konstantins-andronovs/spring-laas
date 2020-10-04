package com.andronovs.loan.repository;

import com.andronovs.loan.entities.Customer;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class CustomerRepository {
    private static final Map<String, Customer> customerData = new HashMap<>();

    public Customer save(Customer customer) {
        customerData.put(customer.getId(), customer);
        return customer;
    }

    public Optional<Customer> getById(String id) {
        return Optional.ofNullable(customerData.get(id));
    }

}

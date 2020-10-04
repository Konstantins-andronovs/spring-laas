package com.andronovs.loan.services;

import com.andronovs.loan.entities.Customer;

public interface CustomerService {
    boolean havePendingLoan(Customer customer);

    Customer getById(String id);

    Customer createOrUpdate(Customer customer);
}

package com.andronovs.loan.services;

import com.andronovs.loan.entities.Customer;
import com.andronovs.loan.enums.LoanStatus;
import com.andronovs.loan.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerServiceImpl(
            CustomerRepository customerRepository
    ) {
        this.customerRepository = customerRepository;
    }

    @Override
    public boolean havePendingLoan(Customer customer) {
        return customer.getLoans().containsValue(LoanStatus.PENDING);
    }

    @Override
    public Customer createOrUpdate(Customer customer) {
        return this.customerRepository.save(customer);
    }

    @Override
    public Customer getById(String id) {
        return this.customerRepository.getById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer with id : " + id + " not found"));
    }
}

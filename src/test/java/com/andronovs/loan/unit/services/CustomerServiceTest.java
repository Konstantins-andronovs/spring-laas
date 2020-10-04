package com.andronovs.loan.unit.services;

import com.andronovs.loan.entities.Customer;
import com.andronovs.loan.enums.LoanStatus;
import com.andronovs.loan.repository.CustomerRepository;
import com.andronovs.loan.services.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;

    @BeforeEach
    public void setUp() {
        Map<UUID, LoanStatus> loans = Collections.singletonMap(UUID.randomUUID(), LoanStatus.PENDING);
        customer = new Customer("customerIdTest", loans);
    }

    @Test
    void createdCustomerHaveLoans() {
        when(customerRepository.save(any(Customer.class))).then(returnsFirstArg());
        Customer savedCustomer = customerService.createOrUpdate(customer);
        assertThat(savedCustomer.getId()).isEqualTo("customerIdTest");
        assertThat(savedCustomer.getLoans()).isNotEmpty();
    }

    @Test
    void retrievedCustomerHaveLoans() {
        when(customerRepository.getById(anyString())).thenReturn(Optional.of(customer));
        Customer retrievedCustomer = customerService.getById("customerIdTest");
        assertThat(retrievedCustomer.getId()).isEqualTo("customerIdTest");
        assertThat(retrievedCustomer.getLoans()).isNotEmpty();
    }

    @Test
    void verifiedCustomerHavePendingLoan() {
        assertTrue(customerService.havePendingLoan(customer));
    }

    @Test
    void verifiedCustomerHaveNoPendingLoan() {
        Map<UUID, LoanStatus> loans = Collections.singletonMap(UUID.randomUUID(), LoanStatus.APPROVED);
        customer = new Customer("customerIdTest", loans);
        assertFalse(customerService.havePendingLoan(customer));
    }
}
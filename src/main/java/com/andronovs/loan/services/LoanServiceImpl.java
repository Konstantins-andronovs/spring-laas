package com.andronovs.loan.services;

import com.andronovs.loan.dto.LoanDTO;
import com.andronovs.loan.entities.Customer;
import com.andronovs.loan.entities.Loan;
import com.andronovs.loan.enums.LoanStatus;
import com.andronovs.loan.repository.LoanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LoanServiceImpl implements LoanService {

    private static final Logger logger = LoggerFactory.getLogger(LoanServiceImpl.class);

    private final LoanRepository loanRepository;
    private final CustomerService customerService;


    @Autowired
    public LoanServiceImpl(
            LoanRepository loanRepository,
            CustomerService customerService) {
        this.loanRepository = loanRepository;
        this.customerService = customerService;
    }

    @Override
    public Loan processLoan(LoanDTO loanDTO) {
        Customer customer;
        Loan loan;
        try {
            customer = this.customerService.getById(loanDTO.getCustomerId());
            if (this.customerService.havePendingLoan(customer)) {
                throw new EntityExistsException("Pending loan for customer: "
                        + loanDTO.getCustomerId() + " already exists");
            }

            loan = this.createLoan(loanDTO);

            Map<UUID, LoanStatus> newLoan = Collections.singletonMap(loan.getId(), loan.getStatus());
            customer.setLoans(Stream.concat(
                    customer.getLoans().entrySet().stream(), newLoan.entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            this.customerService.createOrUpdate(customer);
            logger.info("Customer updated : " + customer.getId());
        } catch (EntityNotFoundException e) {
            loan = this.createLoan(loanDTO);

            customer = new Customer();
            customer.setId(loan.getCustomerId());
            customer.setLoans(Collections.singletonMap(loan.getId(), loan.getStatus()));
            this.customerService.createOrUpdate(customer);
            logger.info("Customer created : " + customer.getId());
        }
        logger.info("Loan contract created : " + loan);

        return loan;
    }


    @Override
    public Loan createLoan(LoanDTO loanDTO) {
        Loan loan = new Loan();
        loan.setCustomerId(loanDTO.getCustomerId());
        loan.setAmount(loanDTO.getAmount().setScale(2, BigDecimal.ROUND_DOWN));
        Map<String, Boolean> approvers = new HashMap<>();
        Arrays.stream(loanDTO.getApprovers())
                .forEach(approver -> approvers.put(approver, false));
        loan.setApprovers(approvers);
        loan.setCreatedDate(LocalDateTime.now());
        loan.setStatus(LoanStatus.PENDING);

        return this.loanRepository.save(loan);
    }

    @Override
    public Loan getCustomerPendingLoan(String customerId) {
        Customer customer = this.customerService.getById(customerId);

        UUID loanId = customer.getLoans().entrySet().stream()
                .filter(entry -> entry.getValue().equals(LoanStatus.PENDING))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElseThrow(() ->
                        new EntityNotFoundException("Customer with id :" + customerId + " does not have Pending loan"));

        return this.getById(loanId);
    }

    @Override
    public DoubleSummaryStatistics getStatistics() {
        LocalDateTime statisticsPeriod = LocalDateTime.now().minusSeconds(60);

        DoubleSummaryStatistics stats = this.loanRepository.getAll()
                .stream()
                .filter(loan -> loan.getCreatedDate().isAfter(statisticsPeriod))
                .map(Loan::getAmount)
                .mapToDouble(BigDecimal::doubleValue)
                .summaryStatistics();
        logger.info("Loan statistics from " + statisticsPeriod.toString() + ": " + stats);

        return stats;
    }

    @Override
    public Loan getById(UUID id) {
        return this.loanRepository.getById(id)
                .orElseThrow(() -> new EntityNotFoundException("Loan with id : " + id + " not found"));
    }
}

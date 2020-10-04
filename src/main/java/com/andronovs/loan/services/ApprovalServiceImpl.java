package com.andronovs.loan.services;

import com.andronovs.loan.dto.ApprovalDTO;
import com.andronovs.loan.entities.Customer;
import com.andronovs.loan.entities.Loan;
import com.andronovs.loan.enums.LoanStatus;
import com.andronovs.loan.exceptions.models.ManagerNotPermittedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ApprovalServiceImpl implements ApprovalService {

    private static final Logger logger = LoggerFactory.getLogger(ApprovalServiceImpl.class);

    private final CustomerService customerService;
    private final LoanService loanService;

    @Autowired
    public ApprovalServiceImpl(
            CustomerService customerService,
            LoanService loanService
    ) {
        this.customerService = customerService;
        this.loanService = loanService;
    }

    @Override
    public Loan approve(ApprovalDTO approval)
            throws ManagerNotPermittedException {

        Loan loan = this.loanService.getCustomerPendingLoan(approval.getCustomerId());
        if (loan.getApprovers().get(approval.getManagerUsername()) == null) {
            throw new ManagerNotPermittedException("Manager with id: "
                    + approval.getManagerUsername() + " is not allowed to approve "
                    + approval.getCustomerId() + " loan");
        }

        loan.getApprovers().put(approval.getManagerUsername(), true);

        logger.info("Loan for customer: " + loan.getCustomerId()
                + " is approved by " + approval.getManagerUsername());

        if (!loan.getApprovers().containsValue(false)) {
            loan.setStatus(LoanStatus.APPROVED);
            this.loanApproved(loan.getId(), approval.getCustomerId());

            logger.info("Loan for customer: " + loan.getCustomerId()
                    + " is fully approved");
        }
        return loan;
    }

    private void loanApproved(UUID loanId, String customerId) {
        Customer customer = this.customerService.getById(customerId);
        Map<UUID, LoanStatus> loans = customer.getLoans().entrySet().stream()
                .filter(loan -> loan.getValue().equals(LoanStatus.PENDING))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        loans.put(loanId, LoanStatus.APPROVED);
        customer.setLoans(loans);
        this.customerService.createOrUpdate(customer);
        // TODO Contact customer;
    }

}

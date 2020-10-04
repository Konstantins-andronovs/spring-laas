package com.andronovs.loan.services;

import com.andronovs.loan.dto.LoanDTO;
import com.andronovs.loan.entities.Loan;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.UUID;

public interface LoanService {
    Loan processLoan(LoanDTO loanDTO);

    Loan getCustomerPendingLoan(String customerId);

    Loan createLoan(LoanDTO loanDTO);

    DoubleSummaryStatistics getStatistics();

    Loan getById(UUID id);
}

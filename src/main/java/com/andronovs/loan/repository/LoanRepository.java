package com.andronovs.loan.repository;

import com.andronovs.loan.entities.Loan;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class LoanRepository {
    private static final Map<UUID, Loan> loanData = new HashMap<>();

    public Loan save(Loan loan) {
        loan.setId(UUID.randomUUID());
        loanData.put(loan.getId(), loan);
        return loan;
    }

    public Optional<Loan> getById(UUID uuid) {
        return Optional.ofNullable(loanData.get(uuid));
    }

    public List<Loan> getAll() {
        return new ArrayList<>(loanData.values());
    }
}

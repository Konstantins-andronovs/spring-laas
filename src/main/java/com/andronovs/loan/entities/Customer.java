package com.andronovs.loan.entities;

import com.andronovs.loan.enums.LoanStatus;
import lombok.*;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    private String id;
    private Map<UUID, LoanStatus> loans;

    @Override
    public String toString() {
        return "Loan{" +
                "id=" + id +
                ", loans='" + loans + '\'' +
                '}';
    }
}

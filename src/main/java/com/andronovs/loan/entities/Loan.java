package com.andronovs.loan.entities;

import com.andronovs.loan.enums.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class Loan implements Serializable {
    private static final long serialVersionUID = -7788619177798333712L;

    private UUID id;
    private String customerId;
    private BigDecimal amount;
    private Map<String, Boolean> approvers;
    private LocalDateTime createdDate;
    private LoanStatus status;

    @Override
    public String toString() {
        return "Loan{" +
                "id=" + id +
                ", customerId='" + customerId + '\'' +
                ", amount='" + amount + '\'' +
                ", approvers='" + approvers + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}


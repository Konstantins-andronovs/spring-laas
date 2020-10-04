package com.andronovs.loan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class LoanDTO implements Serializable {
    private static final long serialVersionUID = -4455620156598999712L;

    @NotBlank(message = "Customer ID is required")
    @Pattern(regexp="^[A-Za-z0-9]{2}-[A-Za-z0-9]{4}-[A-Za-z0-9]{3}$",
            message = "must be in a pattern XX-XXXX-XXX where X is either number or a letter")
    private String customerId;

    @NotNull(message = "Loan amount is required")
    private BigDecimal amount;

    @NotNull(message = "Approvers are required")
    @Size(min = 1, max = 3, message = "There should be from 1 to 3 approvers assigned")
    private String[] approvers;
}

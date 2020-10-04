package com.andronovs.loan.unit.services;

import com.andronovs.loan.dto.ApprovalDTO;
import com.andronovs.loan.entities.Customer;
import com.andronovs.loan.entities.Loan;
import com.andronovs.loan.enums.LoanStatus;
import com.andronovs.loan.exceptions.models.ManagerNotPermittedException;
import com.andronovs.loan.services.ApprovalServiceImpl;
import com.andronovs.loan.services.CustomerService;
import com.andronovs.loan.services.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ApprovalServiceTest {

    @Mock
    private CustomerService customerService;
    @Mock
    private LoanService loanService;

    @InjectMocks
    private ApprovalServiceImpl approvalService;

    private Customer customer;
    private Loan loan;

    @BeforeEach
    public void setUp() {
        Map<UUID, LoanStatus> loans = Collections.singletonMap(UUID.randomUUID(), LoanStatus.PENDING);
        customer = new Customer("customerIdTest", loans);

        Map<String, Boolean> approvals = new HashMap<>();
        approvals.put("managerTest", false);
        approvals.put("managerTest2", false);

        loan = new Loan();
        loan.setCustomerId("customerIdTest");
        loan.setAmount(BigDecimal.TEN.setScale(2, BigDecimal.ROUND_DOWN));
        loan.setApprovers(approvals);
        loan.setCreatedDate(LocalDateTime.now());
        loan.setStatus(LoanStatus.PENDING);
    }

    @Test
    void loanIsPartiallyApproved() throws ManagerNotPermittedException {
        ApprovalDTO approvalDTO = new ApprovalDTO("customerIdTest", "managerTest");
        when(loanService.getCustomerPendingLoan(anyString())).thenReturn(loan);
        Loan approvedLoan = approvalService.approve(approvalDTO);
        assertThat(approvedLoan.getApprovers().get("managerTest")).isTrue();
        assertThat(approvedLoan.getApprovers().get("managerTest2")).isFalse();
    }

    @Test
    void loanIsFullyApproved() throws ManagerNotPermittedException {
        ApprovalDTO approvalDTO = new ApprovalDTO("customerIdTest", "managerTest");
        ApprovalDTO approvalDTO2 = new ApprovalDTO("customerIdTest", "managerTest2");
        when(loanService.getCustomerPendingLoan(anyString())).thenReturn(loan);
        when(customerService.getById(anyString())).thenReturn(customer);
        approvalService.approve(approvalDTO);
        Loan approvedLoan = approvalService.approve(approvalDTO2);
        assertThat(approvedLoan.getApprovers().get("managerTest")).isTrue();
        assertThat(approvedLoan.getApprovers().get("managerTest2")).isTrue();
        assertThat(approvedLoan.getStatus()).isEqualTo(LoanStatus.APPROVED);
    }
}
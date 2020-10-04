package com.andronovs.loan.unit.controllers;

import com.andronovs.loan.controllers.LoanController;
import com.andronovs.loan.dto.ApprovalDTO;
import com.andronovs.loan.dto.LoanDTO;
import com.andronovs.loan.entities.Loan;
import com.andronovs.loan.enums.LoanStatus;
import com.andronovs.loan.exceptions.models.ManagerNotPermittedException;
import com.andronovs.loan.services.ApprovalService;
import com.andronovs.loan.services.LoanService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.apache.commons.lang3.ArrayUtils.toArray;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoanControllerTest {

    @Mock
    private ApprovalService approvalService;
    @Mock
    private LoanService loanService;

    @InjectMocks
    private LoanController loanController;

    private Loan loan;

    @BeforeEach
    public void setUp() {
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
    void loanCreated() {
        LoanDTO loanDTO = new LoanDTO("customerIdTest", BigDecimal.TEN, toArray("manager"));
        when(loanService.processLoan(any(LoanDTO.class))).thenReturn(loan);
        ResponseEntity<Loan> loanResponse = loanController.createLoan(loanDTO);
        assertThat(loanResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(loanResponse.getBody()).getStatus()).isEqualTo(LoanStatus.PENDING);
    }

    @Test
    void loanIsApproved() throws ManagerNotPermittedException {
        ApprovalDTO approvalDTO = new ApprovalDTO("customerIdTest", "managerTest");
        loan.setStatus(LoanStatus.APPROVED);
        when(approvalService.approve(any(ApprovalDTO.class))).thenReturn(loan);
        ResponseEntity<Loan> approvalResponse = loanController.approveLoan(approvalDTO);
        assertThat(approvalResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(approvalResponse.getBody()).getStatus()).isEqualTo(LoanStatus.APPROVED);
    }

    @Test()
    void managerIsNotPermittedToApprove() throws ManagerNotPermittedException {
        ApprovalDTO approvalDTO = new ApprovalDTO("customerIdTest", "managerTest");
        loan.setStatus(LoanStatus.APPROVED);
        when(approvalService.approve(any(ApprovalDTO.class))).thenThrow(ManagerNotPermittedException.class);

        Assertions.assertThrows(ResponseStatusException.class, () -> {
            loanController.approveLoan(approvalDTO);
        });
    }
}
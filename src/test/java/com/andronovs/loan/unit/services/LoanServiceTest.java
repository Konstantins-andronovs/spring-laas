package com.andronovs.loan.unit.services;

import com.andronovs.loan.dto.LoanDTO;
import com.andronovs.loan.entities.Customer;
import com.andronovs.loan.entities.Loan;
import com.andronovs.loan.enums.LoanStatus;
import com.andronovs.loan.repository.LoanRepository;
import com.andronovs.loan.services.CustomerService;
import com.andronovs.loan.services.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.apache.commons.lang3.ArrayUtils.toArray;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;
    @Mock
    private CustomerService customerService;

    @InjectMocks
    private LoanServiceImpl loanService;

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
    void processedLoanHaveCustomerUpdated() {
        when(loanRepository.save(any(Loan.class))).then(returnsFirstArg());
        Map<UUID, LoanStatus> loans = Collections.singletonMap(UUID.randomUUID(), LoanStatus.APPROVED);
        customer = new Customer("customerIdTest", loans);

        when(customerService.getById(anyString())).thenReturn(customer);
        when(customerService.havePendingLoan(any(Customer.class))).thenReturn(false);
        when(customerService.createOrUpdate(any(Customer.class))).thenReturn(customer);

        LoanDTO loanDTO = new LoanDTO("customerIdTest", BigDecimal.TEN, toArray("managerTest"));
        Loan savedLoan = loanService.processLoan(loanDTO);

        assertThat(savedLoan.getCustomerId()).isEqualTo("customerIdTest");
        assertThat(customer.getLoans().size()).isEqualTo(2);

    }

    @Test
    void processedLoanHaveCustomerCreated() {
        when(loanRepository.save(any(Loan.class))).then(returnsFirstArg());
        customer = new Customer("customerIdTest", Collections.singletonMap(UUID.randomUUID(), LoanStatus.PENDING));

        when(customerService.getById(anyString())).thenThrow(new EntityNotFoundException());
        when(customerService.createOrUpdate(any(Customer.class))).thenReturn(customer);

        LoanDTO loanDTO = new LoanDTO("customerIdTest", BigDecimal.TEN, toArray("managerTest"));
        Loan savedLoan = loanService.processLoan(loanDTO);

        assertThat(savedLoan.getCustomerId()).isEqualTo("customerIdTest");
        assertThat(customer.getId()).isEqualTo("customerIdTest");
        assertThat(customer.getLoans().size()).isEqualTo(1);

    }

    @Test
    void createdLoanHaveCustomerId() {
        when(loanRepository.save(any(Loan.class))).then(returnsFirstArg());

        LoanDTO loanDTO = new LoanDTO("customerIdTest", BigDecimal.TEN, toArray("managerTest"));
        Loan savedLoan = loanService.createLoan(loanDTO);

        assertThat(savedLoan.getCustomerId()).isEqualTo("customerIdTest");
        assertThat(savedLoan.getAmount()).isEqualTo(BigDecimal.TEN.setScale(2, BigDecimal.ROUND_DOWN));
        assertThat(savedLoan.getApprovers().get("managerTest")).isEqualTo(false);
        assertThat(savedLoan.getCreatedDate()).isCloseTo(LocalDateTime.now(), within(30, ChronoUnit.SECONDS));
        assertThat(savedLoan.getStatus()).isEqualTo(LoanStatus.PENDING);
    }

    @Test
    void retrievedCustomerHavePendingLoan() {
        when(customerService.getById(anyString())).thenReturn(customer);
        when(loanRepository.getById(any(UUID.class))).thenReturn(Optional.of(loan));
        Loan savedLoan = loanService.getCustomerPendingLoan("customerIdTest");

        assertThat(savedLoan.getCustomerId()).isEqualTo("customerIdTest");
        assertThat(savedLoan.getStatus()).isEqualTo(LoanStatus.PENDING);
    }

    @Test
    void retrievedLoanByIdHaveStatus() {
        UUID id = UUID.randomUUID();
        when(loanRepository.getById(any(UUID.class))).thenReturn(Optional.of(loan));
        Loan savedLoan = loanService.getById(id);

        assertThat(savedLoan.getCustomerId()).isEqualTo("customerIdTest");
        assertThat(savedLoan.getAmount()).isEqualTo(BigDecimal.TEN.setScale(2, BigDecimal.ROUND_DOWN));
        assertThat(savedLoan.getApprovers().get("managerTest")).isEqualTo(false);
        assertThat(savedLoan.getCreatedDate()).isCloseTo(LocalDateTime.now(), within(30, ChronoUnit.SECONDS));
        assertThat(savedLoan.getStatus()).isEqualTo(LoanStatus.PENDING);
    }

    @Test
    void retrievedStatisticsHave() {
        Loan postponedLoan = new Loan();
        postponedLoan.setCustomerId("customerIdTest");
        postponedLoan.setAmount(BigDecimal.TEN.setScale(2, BigDecimal.ROUND_DOWN));
        postponedLoan.setApprovers(Collections.singletonMap("managerTest", false));
        postponedLoan.setCreatedDate(LocalDateTime.now().minusMinutes(10));
        postponedLoan.setStatus(LoanStatus.PENDING);

        List<Loan> loans = new ArrayList<>();
        loans.add(loan);
        loans.add(postponedLoan);

        when(loanRepository.getAll()).thenReturn(loans);
        DoubleSummaryStatistics statistics = loanService.getStatistics();

        assertThat(statistics.getAverage()).isEqualTo(BigDecimal.TEN.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
        assertThat(statistics.getMax()).isEqualTo(BigDecimal.TEN.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
        assertThat(statistics.getMin()).isEqualTo(BigDecimal.TEN.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
        assertThat(statistics.getSum()).isEqualTo(BigDecimal.TEN.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
        assertThat(statistics.getCount()).isEqualTo(1L);

    }

}
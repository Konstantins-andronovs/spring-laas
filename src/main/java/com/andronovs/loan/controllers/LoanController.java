package com.andronovs.loan.controllers;

import com.andronovs.loan.dto.ApprovalDTO;
import com.andronovs.loan.dto.LoanDTO;
import com.andronovs.loan.entities.Loan;
import com.andronovs.loan.exceptions.models.ManagerNotPermittedException;
import com.andronovs.loan.services.ApprovalService;
import com.andronovs.loan.services.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api")
public class LoanController {

    private final ApprovalService approvalService;
    private final LoanService loanService;
    private final WebClient webClient;

    @Autowired
    public LoanController(
            LoanService loanService,
            ApprovalService approvalService,
            WebClient webClient
    ) {
        this.loanService = loanService;
        this.approvalService = approvalService;
        this.webClient = webClient;
    }

    @PreAuthorize("hasRole('BROKER')")
    @PostMapping(value = "/loan", consumes = "application/json", produces = "application/json")
    public @ResponseBody
    ResponseEntity<Loan> createLoan(@Valid @RequestBody LoanDTO loanDTO) {
        return ResponseEntity.ok(loanService.processLoan(loanDTO));
    }

    @PreAuthorize("hasRole('BROKER')")
    @PostMapping(value = "/loans")
    public @ResponseBody
    List<ResponseEntity<Loan>> createLoans(@RequestBody List<@Valid LoanDTO> loanDTOList) {
        // TODO Research Spring Batch for bulk upload
        try {
            return loanDTOList.stream()
                    .map(loanDTO -> webClient.post()
                            .uri("/api/loan")
                            .body(Mono.just(loanDTO), Loan.class)
                            .retrieve()
                            .bodyToMono(Loan.class)
                            .blockOptional())
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(ResponseEntity::ok)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "There was an error while creating loans", e);
        }
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping(value = "/loans/approve", consumes = "application/json", produces = "application/json")
    public @ResponseBody
    ResponseEntity<Loan> approveLoan(@Valid @RequestBody ApprovalDTO approvalDTO) {
        try {
            return ResponseEntity.ok(approvalService.approve(approvalDTO));
        } catch (ManagerNotPermittedException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "This manager is not allowed to approve this request", e);
        }
    }

    @PreAuthorize("hasRole('BROKER') OR hasRole('MANAGER')")
    @GetMapping(value = "/loans/stats", produces = "application/json")
    public @ResponseBody
    ResponseEntity<DoubleSummaryStatistics> getStatistics() {
        return ResponseEntity.ok(loanService.getStatistics());
    }

}

package com.andronovs.loan.services;

import com.andronovs.loan.dto.ApprovalDTO;
import com.andronovs.loan.entities.Loan;
import com.andronovs.loan.exceptions.models.ManagerNotPermittedException;

public interface ApprovalService {

    Loan approve(ApprovalDTO approval) throws ManagerNotPermittedException;

}

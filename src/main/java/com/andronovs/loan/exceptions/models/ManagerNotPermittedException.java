package com.andronovs.loan.exceptions.models;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Selected manager is not eligible to approve this loan")
public class ManagerNotPermittedException extends Exception {
    public ManagerNotPermittedException(String errorMessage) {
        super(errorMessage);
    }
}

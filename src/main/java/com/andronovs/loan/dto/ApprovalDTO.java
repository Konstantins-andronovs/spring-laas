package com.andronovs.loan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
@Getter
@Setter
@AllArgsConstructor
public class ApprovalDTO implements Serializable {

    @NotBlank(message = "Customer ID is required")
    @Pattern(regexp="^[A-Za-z0-9]{2}-[A-Za-z0-9]{4}-[A-Za-z0-9]{3}$")
    private String customerId;
    @NotBlank(message = "Manager username is required")
    private String managerUsername;
}

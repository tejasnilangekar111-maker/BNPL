package com.example.BNPL.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class EmiCalculationRequest {
    private BigDecimal principalAmount;
    private Integer tenureMonths; // 3,6,12,24
    private Integer userCreditScore; // to determine interest rate
}

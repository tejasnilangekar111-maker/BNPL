package com.example.BNPL.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EmiCalculationResponse {
    private BigDecimal monthlyEmi;
    private BigDecimal totalInterest;
    private BigDecimal totalPayable;
    private BigDecimal interestRateApplied;
    private List<AmortizationEntry> schedule;

    @Data @Builder
    public static class AmortizationEntry {
        private int month;
        private BigDecimal principal;
        private BigDecimal interest;
        private BigDecimal balance;
    }}

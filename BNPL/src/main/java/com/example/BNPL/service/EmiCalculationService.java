package com.example.BNPL.service;

import com.example.BNPL.dto.EmiCalculationResponse;
import com.example.BNPL.dto.EmiCalculationResponse.AmortizationEntry;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmiCalculationService {

    /**
     * EMI Formula (Reducing Balance):
     * EMI = [P * r * (1+r)^n] / [(1+r)^n – 1]
     * P = principal
     * r = monthly interest rate (annualRate / 12 / 100)
     * n = number of months
     */
    public EmiCalculationResponse calculateEmi(BigDecimal principal,
                                               BigDecimal annualInterestRatePercent,
                                               int tenureMonths) {
        // Convert annual % to monthly decimal (e.g., 12.5% -> 0.0125/12)
        BigDecimal monthlyRate = annualInterestRatePercent
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);

        // (1+r)^n
        BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
        BigDecimal factor = onePlusR.pow(tenureMonths);

        // Numerator: P * r * (1+r)^n
        BigDecimal numerator = principal
                .multiply(monthlyRate)
                .multiply(factor);

        // Denominator: (1+r)^n - 1
        BigDecimal denominator = factor.subtract(BigDecimal.ONE);

        // EMI
        BigDecimal emi = numerator
                .divide(denominator, 2, RoundingMode.HALF_UP);

        // Build amortization schedule
        List<AmortizationEntry> schedule = new ArrayList<>();
        BigDecimal remainingBalance = principal;
        BigDecimal totalInterest = BigDecimal.ZERO;

        for (int month = 1; month <= tenureMonths; month++) {
            // Interest portion for this month
            BigDecimal interestPart = remainingBalance
                    .multiply(monthlyRate)
                    .setScale(2, RoundingMode.HALF_UP);

            // Principal portion
            BigDecimal principalPart = emi.subtract(interestPart);
            if (month == tenureMonths) {
                principalPart = remainingBalance; // adjust for rounding
            }

            remainingBalance = remainingBalance.subtract(principalPart);
            totalInterest = totalInterest.add(interestPart);

            schedule.add(AmortizationEntry.builder()
                    .month(month)
                    .principal(principalPart)
                    .interest(interestPart)
                    .balance(remainingBalance.max(BigDecimal.ZERO))
                    .build());
        }

        BigDecimal totalPayable = principal.add(totalInterest);

        return EmiCalculationResponse.builder()
                .monthlyEmi(emi)
                .totalInterest(totalInterest)
                .totalPayable(totalPayable)
                .interestRateApplied(annualInterestRatePercent)
                .schedule(schedule)
                .build();
    }
}

package com.example.BNPL.service;

import com.example.BNPL.dto.CreateOrderRequest;
import com.example.BNPL.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class RiskAssessmentService {

    @Value("${bnpl.min-credit-score}")
    private int minCreditScore;

    @Value("${bnpl.max-debt-to-income}")
    private double maxDebtToIncome;

    public boolean isApproved(User user, CreateOrderRequest request) {
        // 1. Credit Score check
        if (user.getCreditScore() < minCreditScore) return false;

        // 2. Affordability: EMI should not exceed maxDebtToIncome * monthlyIncome
        if (user.getMonthlyIncome() == null) return false;

        // Rough EMI estimate (simplified – real calc happens in EmiCalculationService)
        BigDecimal estimatedEmi = request.getAmount()
                .divide(BigDecimal.valueOf(request.getTenureMonths()), 2, RoundingMode.HALF_UP);

        BigDecimal maxAllowedEmi = user.getMonthlyIncome()
                .multiply(BigDecimal.valueOf(maxDebtToIncome));

        return estimatedEmi.compareTo(maxAllowedEmi) <= 0;
    }

    public BigDecimal determineInterestRate(int creditScore) {
        if (creditScore >= 750) return BigDecimal.ZERO;       // 0%
        if (creditScore >= 700) return new BigDecimal("8.5"); // 8.5% p.a.
        if (creditScore >= 650) return new BigDecimal("14.0");
        return new BigDecimal("24.0"); // high risk
    }
}

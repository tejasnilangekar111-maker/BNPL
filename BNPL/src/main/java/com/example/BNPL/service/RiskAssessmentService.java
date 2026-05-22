package com.example.BNPL.service;

import com.example.BNPL.dto.CreateOrderRequest;
import com.example.BNPL.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class RiskAssessmentService {

    @Value("${bnpl.min-credit-score}")
    private int minCreditScore;

    @Value("${bnpl.max-debt-to-income}")
    private double maxDebtToIncome;

    public boolean isApproved(User user, CreateOrderRequest request) {
        log.debug("Assessing risk - userId: {}, creditScore: {}, amount: {}, tenure: {}",
                user.getUserId(), user.getCreditScore(), request.getAmount(), request.getTenureMonths());

        if (user.getCreditScore() < minCreditScore) {
            log.warn("Risk check FAILED - creditScore {} below minimum {}", user.getCreditScore(), minCreditScore);
            return false;
        }

        if (user.getMonthlyIncome() == null) {
            log.warn("Risk check FAILED - monthly income not set for userId: {}", user.getUserId());
            return false;
        }

        BigDecimal estimatedEmi = request.getAmount()
                .divide(BigDecimal.valueOf(request.getTenureMonths()), 2, RoundingMode.HALF_UP);

        BigDecimal maxAllowedEmi = user.getMonthlyIncome()
                .multiply(BigDecimal.valueOf(maxDebtToIncome));

        boolean approved = estimatedEmi.compareTo(maxAllowedEmi) <= 0;
        log.info("Risk check {} - estimatedEmi: {}, maxAllowedEmi: {}, userId: {}",
                approved ? "PASSED" : "FAILED", estimatedEmi, maxAllowedEmi, user.getUserId());
        return approved;
    }

    public BigDecimal determineInterestRate(int creditScore) {
        BigDecimal rate;
        if (creditScore >= 750) rate = BigDecimal.ZERO;
        else if (creditScore >= 700) rate = new BigDecimal("8.5");
        else if (creditScore >= 650) rate = new BigDecimal("14.0");
        else rate = new BigDecimal("24.0");
        log.debug("Interest rate for creditScore {}: {}%", creditScore, rate);
        return rate;
    }
}

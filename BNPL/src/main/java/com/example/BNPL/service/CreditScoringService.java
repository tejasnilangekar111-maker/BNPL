package com.example.BNPL.service;

import com.example.BNPL.entity.CreditHistory;
import com.example.BNPL.entity.User;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CreditScoringService {

    /**
     * Calculates credit score (300–850) based on weighted factors.
     */
    public int calculateCreditScore(User user, List<CreditHistory> history) {
        int baseScore = 300;
        int maxAddOn = 550; // total points distributed across factors

        // 1. Payment History (35% of 550 ≈ 192 points)
        double phScore = calculatePaymentHistoryScore(history) * 0.35;

        // 2. Outstanding Debt / Utilization (30% of 550 ≈ 165 points)
        double debtScore = calculateDebtToIncomeScore(user) * 0.30;

        // 3. Credit History Length (15% of 550 ≈ 82 points)
        double chlScore = calculateCreditHistoryLengthScore(user) * 0.15;

        // 4. Types of Credit (10% of 550 ≈ 55 points) – simplified: based on number of previous loans
        double tcScore = calculateTypesOfCreditScore(history) * 0.10;

        // 5. Recent Inquiries (10% of 550 ≈ 55 points) – based on recent new loan requests
        double riScore = calculateRecentInquiriesScore(history) * 0.10;

        int total = baseScore + (int) Math.round(phScore + debtScore + chlScore + tcScore + riScore);
        return Math.min(850, Math.max(300, total));
    }

    private double calculatePaymentHistoryScore(List<CreditHistory> history) {
        if (history.isEmpty()) return 50; // neutral if no history
        long onTime = history.stream()
                .filter(h -> h.getImpactOnScore() > 0)
                .count();
        double ratio = (double) onTime / history.size();
        return ratio * 100; // 0–100 scale
    }

    private double calculateDebtToIncomeScore(User user) {
        if (user.getMonthlyIncome() == null || user.getMonthlyIncome().compareTo(BigDecimal.ZERO) == 0)
            return 0;
        // Here we would sum active EMIs; simplified: assume current obligations from credit history
        BigDecimal totalMonthlyObligation = calculateActiveMonthlyObligation(user);
        BigDecimal ratio = totalMonthlyObligation
                .divide(user.getMonthlyIncome(), 4, RoundingMode.HALF_UP);
        if (ratio.compareTo(BigDecimal.valueOf(0.3)) <= 0) return 100;
        if (ratio.compareTo(BigDecimal.valueOf(0.5)) <= 0) return 70;
        return 30; // high debt
    }

    private double calculateCreditHistoryLengthScore(User user) {
        long months = java.time.temporal.ChronoUnit.MONTHS
                .between(user.getRegistrationDate(), LocalDateTime.now());
        if (months > 60) return 100;
        if (months > 24) return 80;
        if (months > 12) return 60;
        return 30;
    }

    private double calculateTypesOfCreditScore(List<CreditHistory> history) {
        long distinctTypes = history.stream()
                .map(CreditHistory::getTransactionType)
                .distinct()
                .count();
        return Math.min(100, distinctTypes * 20); // up to 5 types
    }

    private double calculateRecentInquiriesScore(List<CreditHistory> history) {
        long recent = history.stream()
                .filter(h -> h.getDate().isAfter(LocalDateTime.now().minusMonths(6)))
                .filter(h -> h.getTransactionType() == com.example.BNPL.entity.TransactionType.NEW_LOAN)
                .count();
        if (recent == 0) return 100;
        if (recent <= 2) return 80;
        return 40; // too many recent inquiries
    }

    private BigDecimal calculateActiveMonthlyObligation(User user) {
        // Simplified: sum of last known EMI payments from history marked as active
        // In real app, query active BnplPlans
        return BigDecimal.ZERO; // placeholder – wire to actual active plans if needed
    }
}

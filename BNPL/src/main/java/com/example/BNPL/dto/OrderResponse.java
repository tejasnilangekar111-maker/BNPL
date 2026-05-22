package com.example.BNPL.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long orderId;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private String status;

    // BNPL Plan details
    private BigDecimal principalAmount;
    private BigDecimal interestRate;
    private Integer tenureMonths;
    private BigDecimal emiAmount;
    private BigDecimal totalPayable;

    // EMI Schedule
    private List<EmiEntry> emiSchedule;

    @Data
    @Builder
    public static class EmiEntry {
        private Long scheduleId;
        private Integer emiNumber;
        private LocalDate dueDate;
        private BigDecimal amount;
        private String status;
    }
}

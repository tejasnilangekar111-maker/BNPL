package com.example.BNPL.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class AdminStatsResponse {
    private long totalUsers;
    private long totalOrders;
    private long activeLoans;
    private BigDecimal totalAmountDisbursed;
    private long pendingEmis;
    private long overdueEmis;
}

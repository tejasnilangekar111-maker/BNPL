package com.example.BNPL.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateOrderRequest {
    private Long merchantId;
    private Long productId;
    private BigDecimal amount; // final amount
    private Integer tenureMonths;
}

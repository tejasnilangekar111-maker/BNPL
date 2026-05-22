package com.example.BNPL.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private String paymentMethod; // CARD, UPI, NETBANKING
}

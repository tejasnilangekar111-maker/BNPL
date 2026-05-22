package com.example.BNPL.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RegisterRequest {
    private String email;
    private String phone;
    private String address;
    private String password;
    private BigDecimal monthlyIncome;
}

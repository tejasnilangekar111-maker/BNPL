package com.example.BNPL.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class UserProfileResponse {
    private Long userId;
    private String email;
    private String phone;
    private String address;
    private Integer creditScore;
    private BigDecimal monthlyIncome;
    private String role;
}

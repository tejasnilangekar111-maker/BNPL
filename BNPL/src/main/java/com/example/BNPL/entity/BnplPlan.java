package com.example.BNPL.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "bnpl_plans")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class BnplPlan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "principal_amount")
    private BigDecimal principalAmount;

    @Column(name = "interest_rate")
    private BigDecimal interestRate; // annual % stored as decimal (e.g., 12.5 for 12.5%)

    @Column(name = "tenure_months")
    private Integer tenureMonths;

    @Column(name = "emi_amount")
    private BigDecimal emiAmount;

    @OneToMany(mappedBy = "bnplPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EmiSchedule> emiSchedules;
}

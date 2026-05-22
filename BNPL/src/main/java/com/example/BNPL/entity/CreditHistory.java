package com.example.BNPL.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "credit_history")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CreditHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType; // EMI_PAYMENT, DEFAULT, NEW_LOAN, EARLY_PAYMENT

    private BigDecimal amount;

    @Column(name = "impact_on_score")
    private Integer impactOnScore; // positive or negative points

    private LocalDateTime date = LocalDateTime.now();
}

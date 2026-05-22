package com.example.BNPL.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "emi_schedule")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class EmiSchedule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private BnplPlan bnplPlan;

    @Column(name = "emi_number")
    private Integer emiNumber;

    @Column(name = "due_date")
    private LocalDate dueDate;

    private BigDecimal amount;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private EmiStatus status = EmiStatus.PENDING; // PENDING, PAID, OVERDUE

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Builder.Default
    @Column(name = "late_fee")
    private BigDecimal lateFee = BigDecimal.ZERO;
}

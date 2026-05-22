package com.example.BNPL.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private EmiSchedule emiSchedule;

    private BigDecimal amount;
    private String paymentMethod; // CARD, UPI, NETBANKING
    private String transactionId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // SUCCESS, FAILED, PENDING

    @Builder.Default
    private LocalDateTime paymentTime = LocalDateTime.now();
}

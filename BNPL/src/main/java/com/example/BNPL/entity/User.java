package com.example.BNPL.entity;

import jakarta.persistence.*;
import com.example.BNPL.entity.Order;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true, nullable = false)
    private String email;

    private String phone;
    private String address;
    private String passwordHash;

    @Column(name = "registration_date")
    @Builder.Default
    private LocalDateTime registrationDate = LocalDateTime.now();

    @Column(name = "credit_score")
    @Builder.Default
    private Integer creditScore = 650; // default

    @Column(name = "monthly_income")
    private BigDecimal monthlyIncome;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.CUSTOMER; // CUSTOMER, MERCHANT, ADMIN

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CreditHistory> creditHistories;
}
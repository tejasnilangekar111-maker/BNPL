package com.example.BNPL.controller;

import com.example.BNPL.dto.AdminStatsResponse;
import com.example.BNPL.dto.OrderResponse;
import com.example.BNPL.dto.UserProfileResponse;
import com.example.BNPL.entity.EmiStatus;
import com.example.BNPL.entity.Order;
import com.example.BNPL.entity.OrderStatus;
import com.example.BNPL.repository.EmiScheduleRepository;
import com.example.BNPL.repository.OrderRepository;
import com.example.BNPL.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final EmiScheduleRepository emiScheduleRepository;

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getStats() {
        log.info("Admin fetching platform stats");

        long totalUsers = userRepository.count();
        List<Order> allOrders = orderRepository.findAll();
        long totalOrders = allOrders.size();
        long activeLoans = allOrders.stream().filter(o -> o.getStatus() == OrderStatus.APPROVED).count();

        BigDecimal totalDisbursed = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.APPROVED)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long pendingEmis = emiScheduleRepository.countByStatus(EmiStatus.PENDING);
        long overdueEmis = emiScheduleRepository.countByStatus(EmiStatus.OVERDUE);

        return ResponseEntity.ok(AdminStatsResponse.builder()
                .totalUsers(totalUsers)
                .totalOrders(totalOrders)
                .activeLoans(activeLoans)
                .totalAmountDisbursed(totalDisbursed)
                .pendingEmis(pendingEmis)
                .overdueEmis(overdueEmis)
                .build());
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        log.info("Admin fetching all users");
        List<UserProfileResponse> users = userRepository.findAll().stream()
                .map(u -> UserProfileResponse.builder()
                        .userId(u.getUserId())
                        .email(u.getEmail())
                        .phone(u.getPhone())
                        .address(u.getAddress())
                        .creditScore(u.getCreditScore())
                        .monthlyIncome(u.getMonthlyIncome())
                        .role(u.getRole().name())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        log.info("Admin fetching all orders");
        List<OrderResponse> orders = orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    private OrderResponse mapToResponse(Order order) {
        OrderResponse.OrderResponseBuilder builder = OrderResponse.builder()
                .orderId(order.getOrderId())
                .totalAmount(order.getTotalAmount())
                .orderDate(order.getOrderDate())
                .status(order.getStatus().name());

        if (order.getBnplPlan() != null) {
            var plan = order.getBnplPlan();
            BigDecimal totalPayable = plan.getEmiAmount()
                    .multiply(BigDecimal.valueOf(plan.getTenureMonths()));
            builder.principalAmount(plan.getPrincipalAmount())
                    .interestRate(plan.getInterestRate())
                    .tenureMonths(plan.getTenureMonths())
                    .emiAmount(plan.getEmiAmount())
                    .totalPayable(totalPayable);

            if (plan.getEmiSchedules() != null) {
                builder.emiSchedule(plan.getEmiSchedules().stream()
                        .map(emi -> OrderResponse.EmiEntry.builder()
                                .emiNumber(emi.getEmiNumber())
                                .dueDate(emi.getDueDate())
                                .amount(emi.getAmount())
                                .status(emi.getStatus().name())
                                .build())
                        .collect(Collectors.toList()));
            }
        }
        return builder.build();
    }
}

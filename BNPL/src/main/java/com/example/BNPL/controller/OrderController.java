package com.example.BNPL.controller;

import com.example.BNPL.dto.CreateOrderRequest;
import com.example.BNPL.dto.OrderResponse;
import com.example.BNPL.entity.Order;
import com.example.BNPL.repository.OrderRepository;
import com.example.BNPL.repository.UserRepository;
import com.example.BNPL.service.BnplOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final BnplOrderService bnplOrderService;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody CreateOrderRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        log.info("Order creation request from: {}, amount: {}", email, request.getAmount());

        Order order = bnplOrderService.processOrder(request, email);
        return ResponseEntity.ok(mapToResponse(order));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrders(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        log.info("Fetching orders for: {}", email);

        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getUserId();

        List<OrderResponse> orders = orderRepository.findByUserUserId(userId).stream()
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
                                .scheduleId(emi.getScheduleId())
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

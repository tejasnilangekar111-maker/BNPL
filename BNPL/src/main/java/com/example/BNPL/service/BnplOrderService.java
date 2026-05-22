package com.example.BNPL.service;

import com.example.BNPL.dto.CreateOrderRequest;
import com.example.BNPL.dto.EmiCalculationResponse;
import com.example.BNPL.entity.*;
import com.example.BNPL.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BnplOrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final BnplPlanRepository planRepository;
    private final EmiScheduleRepository emiScheduleRepository;
    private final EmiCalculationService emiService;
    private final RiskAssessmentService riskService;
    private final CreditHistoryRepository creditHistoryRepository;

    @Transactional
    public Order processOrder(CreateOrderRequest req, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Risk check
        if (!riskService.isApproved(user, req)) {
            throw new RuntimeException("Order rejected: Risk assessment failed");
        }

        // Determine interest rate based on score
        BigDecimal interestRate = riskService.determineInterestRate(user.getCreditScore());

        // Calculate EMI details
        EmiCalculationResponse emiResp = emiService.calculateEmi(
                req.getAmount(), interestRate, req.getTenureMonths());

        // Create Order
        Order order = Order.builder()
                .user(user)
                .totalAmount(req.getAmount())
                .status(OrderStatus.APPROVED)
                .build();
        order = orderRepository.save(order);

        // Create BNPL Plan
        BnplPlan plan = BnplPlan.builder()
                .order(order)
                .principalAmount(req.getAmount())
                .interestRate(interestRate)
                .tenureMonths(req.getTenureMonths())
                .emiAmount(emiResp.getMonthlyEmi())
                .build();
        plan = planRepository.save(plan);

        // Generate EMI Schedule
        List<EmiSchedule> schedules = new ArrayList<>();
        LocalDate startDate = LocalDate.now().plusMonths(1).withDayOfMonth(1); // first of next month
        for (int i = 0; i < req.getTenureMonths(); i++) {
            EmiSchedule sch = EmiSchedule.builder()
                    .bnplPlan(plan)
                    .emiNumber(i + 1)
                    .dueDate(startDate.plusMonths(i))
                    .amount(emiResp.getMonthlyEmi())
                    .status(EmiStatus.PENDING)
                    .build();
            schedules.add(sch);
        }
        emiScheduleRepository.saveAll(schedules);

        // Log credit history for new loan
        creditHistoryRepository.save(java.util.Objects.requireNonNull(CreditHistory.builder()
                .user(user)
                .transactionType(TransactionType.NEW_LOAN)
                .amount(req.getAmount())
                .impactOnScore(-5) // slight dip for new credit inquiry
                .build()));

        order.setBnplPlan(plan);
        return order;
    }
}

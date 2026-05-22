package com.example.BNPL.service;

import com.example.BNPL.dto.PaymentRequest;
import com.example.BNPL.dto.PaymentResponse;
import com.example.BNPL.entity.*;
import com.example.BNPL.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final EmiScheduleRepository emiScheduleRepository;
    private final PaymentRepository paymentRepository;
    private final CreditHistoryRepository creditHistoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public PaymentResponse payEmi(Long scheduleId, PaymentRequest request, String userEmail) {
        log.info("Payment initiated - scheduleId: {}, method: {}, user: {}", scheduleId, request.getPaymentMethod(), userEmail);

        EmiSchedule schedule = emiScheduleRepository.findByIdWithUser(scheduleId)
                .orElseThrow(() -> new RuntimeException("EMI schedule not found"));

        // Verify ownership
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Long ownerId = schedule.getBnplPlan().getOrder().getUser().getUserId();
        if (!ownerId.equals(user.getUserId())) {
            log.warn("Unauthorized payment attempt - scheduleId: {}, userId: {}", scheduleId, user.getUserId());
            throw new RuntimeException("Unauthorized: This EMI does not belong to you");
        }

        if (schedule.getStatus() == EmiStatus.PAID) {
            log.warn("EMI already paid - scheduleId: {}", scheduleId);
            throw new RuntimeException("This EMI is already paid");
        }

        // Generate transaction ID
        String transactionId = "TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();

        // Record payment
        Payment payment = Payment.builder()
                .emiSchedule(schedule)
                .amount(schedule.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .transactionId(transactionId)
                .status(PaymentStatus.SUCCESS)
                .paymentTime(LocalDateTime.now())
                .build();
        payment = paymentRepository.save(payment);

        // Update EMI status
        schedule.setStatus(EmiStatus.PAID);
        schedule.setPaymentDate(LocalDateTime.now());
        emiScheduleRepository.save(schedule);
        log.info("EMI marked as PAID - scheduleId: {}, transactionId: {}", scheduleId, transactionId);

        // Log positive credit history
        creditHistoryRepository.save(CreditHistory.builder()
                .user(user)
                .transactionType(TransactionType.EMI_PAYMENT)
                .amount(schedule.getAmount())
                .impactOnScore(+5)
                .build());

        // Update credit score
        int newScore = Math.min(850, user.getCreditScore() + 5);
        user.setCreditScore(newScore);
        userRepository.save(user);
        log.info("Credit score updated for userId: {} -> {}", user.getUserId(), newScore);

        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .scheduleId(scheduleId)
                .amount(schedule.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .transactionId(transactionId)
                .status("SUCCESS")
                .paymentTime(payment.getPaymentTime())
                .message("Payment successful! EMI marked as paid.")
                .build();
    }
}

package com.example.BNPL.controller;

import com.example.BNPL.dto.PaymentRequest;
import com.example.BNPL.dto.PaymentResponse;
import com.example.BNPL.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{scheduleId}")
    public ResponseEntity<PaymentResponse> payEmi(
            @PathVariable Long scheduleId,
            @RequestBody PaymentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("Pay EMI request - scheduleId: {}, user: {}", scheduleId, userDetails.getUsername());
        PaymentResponse response = paymentService.payEmi(scheduleId, request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
}

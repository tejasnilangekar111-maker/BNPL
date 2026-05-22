package com.example.BNPL.service;
import com.example.BNPL.dto.RegisterRequest;
import com.example.BNPL.entity.User;
import com.example.BNPL.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.lang.NonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CreditScoringService creditScoringService;

    @SuppressWarnings("null")
    public User register(RegisterRequest req) {
        log.debug("Creating new user account for email: {}", req.getEmail());
        User user = User.builder()
                .email(req.getEmail())
                .phone(req.getPhone())
                .address(req.getAddress())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .monthlyIncome(req.getMonthlyIncome())
                .creditScore(650) // default starting score
                .build();
        User saved = userRepository.save(user);
        log.info("New user saved to DB - userId: {}, email: {}", saved.getUserId(), saved.getEmail());
        return saved;
    }

    public void updateCreditScore(@NonNull Long userId) {
        log.info("Updating credit score for userId: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        int oldScore = user.getCreditScore();
        int newScore = creditScoringService.calculateCreditScore(user, user.getCreditHistories());
        user.setCreditScore(newScore);
        userRepository.save(user);
        log.info("Credit score updated for userId: {} - {} -> {}", userId, oldScore, newScore);
    }

}
